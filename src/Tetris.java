import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

class MyFrame extends JFrame
{
    public MyFrame()
    {
        Tetris tetris = new Tetris();
        add(tetris);
        setTitle("Game \"Tetris\"");
        setBounds(0, 0, 580, 555);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        MyFrame myFrame = new MyFrame();
        myFrame.setLocationRelativeTo(null);
        myFrame.setResizable(false);
    }
}

public class Tetris extends JPanel
{
    public static final int MAX_SIZE = 6; // максимальное кол-во точек в фигуре
    private static final int BOARD_WIDTH = 25;
    private static final int BOARD_HEIGHT = 35;
    private static final int SQUARE_SIZE = 15;
    private BufferedImage gameOver = null;

    private Timer tmDraw;
    private boolean isFell = false;
    private boolean isStarted = false;
    private boolean game_is_over = false;
    private int curX = 0; // положение текущей фигуры на доске
    private int curY = 0;
    private int score = 0; // считается как число удалённых квадратов (30 за линию)
    private Shape currentShape;
    private ShapeForm[] forms; //массив форм фигур, упавших на доску
    private JLabel lb;
    private JButton btn1, btn2;

    public Tetris()
    {
        tmDraw = new Timer(300, e -> {
            if (isFell) {
                isFell = false;
                newShape();
            }
            else if(!game_is_over) oneLineDown();
        });
        setFocusable(true);
        setLayout(null);

        lb = new JLabel("Score: " + score);
        lb.setForeground(Color.DARK_GRAY);
        lb.setFont(new Font("comic sans MS", 0, 30));
        lb.setBounds(410, 450, 130, 50);
        add(lb);

        btn1 = new JButton("NEW GAME");
        btn1.setForeground(new Color(47, 126, 154));
        btn1.setFont(new Font("comic sans MS", 0, 16));
        btn1.setBounds(410, 30, 130, 50);
        btn1.addActionListener(arg0 -> {
            start();
        });
        add(btn1);

        btn2 = new JButton("EXIT");
        btn2.setForeground(new Color(232, 95, 76));
        btn2.setFont(new Font("comic sans MS", 0, 16));
        btn2.setBounds(410, 100, 130, 50);
        btn2.addActionListener(arg0 -> System.exit(0));
        add(btn2);

        currentShape = new Shape();

        forms = new ShapeForm[BOARD_WIDTH * BOARD_HEIGHT];

        start();

        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent key)
            {
                if (!isStarted || currentShape.currentForm == ShapeForm.EmptyShape) return;

                int keyCode = key.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        tryMove(currentShape, curX - 1, curY);
                        break;
                    case KeyEvent.VK_RIGHT:
                        tryMove(currentShape, curX + 1, curY);
                        break;
                    case KeyEvent.VK_DOWN:
                        throwDown();
                        break;
                    case KeyEvent.VK_UP:
                        tryMove(currentShape.turn(), curX, curY);
                        break;
                }
            }
        });
    }

    // начало игры
    public void start()
    {
        grabFocus();
        game_is_over = false;
        isStarted = true;
        isFell = false;
        score = 0;
        lb.setText("Score: " + score);
        clearBoard();
        newShape();
        tmDraw.start();
    }

    // очищает доску (заполняет пустыми фигурами)
    private void clearBoard()
    {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            forms[i] = ShapeForm.EmptyShape;
        }
    }

    // создаёт новую фигуру и помещает в позицию начала падения
    public void newShape()
    {
        currentShape.getRandomForm();
        curX = BOARD_WIDTH / 2;
        curY = BOARD_HEIGHT + currentShape.minY();

        if (!tryMove(currentShape, curX, curY-1)) {
            isStarted = false;
            game_is_over = true;
        }
    }

    // проверяет есть ли куда падать текущей фигуре и заставляет падать
    private void oneLineDown()
    {
        if (!tryMove(currentShape, curX, curY - 1)) shapeFalled();
    }

    // вызывается, если фигура упала
    // добавляет фигуру в массив упавших, вызывает проверку заполненных линий и новую фигуру по завершению
    private void shapeFalled()
    {
        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            int x = curX + currentShape.getX(i);
            int y = curY - currentShape.getY(i);
            forms[y * BOARD_WIDTH + x] = currentShape.currentForm;
        }

        dropFullLines();

        if (!isFell) {
            newShape();
        }
    }

    // отрисовывает квадраты для форм
    private void drawSquare(Graphics gr, int x, int y)
    {
        gr.setColor(new Color(232, 95, 76));
        gr.fillRect(x + 1, y + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
        gr.drawLine(x, y + SQUARE_SIZE - 1, x, y);
        gr.drawLine(x, y, x + SQUARE_SIZE - 1, y);
        gr.setColor(new Color(51, 52, 55));
        gr.drawLine(x + 1, y + SQUARE_SIZE - 1, x + SQUARE_SIZE - 1, y + SQUARE_SIZE - 1);
        gr.drawLine(x + SQUARE_SIZE - 1, y + SQUARE_SIZE - 1, x + SQUARE_SIZE - 1, y + 1);
    }

    @Override
    public void paint(Graphics gr)
    {
        super.paint(gr);

        gr.setColor(new Color(47, 126, 154));
        gr.fillRect(0, 0, 375, 526);

        gr.setColor(new Color(155, 155, 155));
        for (int i = 0; i <= BOARD_WIDTH; i++) // колодец 30 в ширину, 40 в высоту, клетка 10 на 10
        {
            gr.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE, 526);
            for (int j = 0; j <= BOARD_HEIGHT; j++) {
                gr.drawLine(0, j * SQUARE_SIZE, 375, j * SQUARE_SIZE);
            }
        }

        for (int i = 0; i < BOARD_HEIGHT; i++) { // отрисовка упавших фигур
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                ShapeForm shape = shapeBelow(j, BOARD_HEIGHT - i - 1);

                if (shape != ShapeForm.EmptyShape) {
                    drawSquare(gr, j * SQUARE_SIZE, i * SQUARE_SIZE);
                }
            }
        }

        if (currentShape.currentForm != ShapeForm.EmptyShape) { // отрисовка падающей фигуры
            for (int i = 0; i < Tetris.MAX_SIZE; ++i) {
                int x = curX + currentShape.getX(i);
                int y = curY - currentShape.getY(i);
                drawSquare(gr, x * SQUARE_SIZE, (BOARD_HEIGHT - y - 1) * SQUARE_SIZE);
            }
        }

        if (game_is_over){
            try {
                gameOver = ImageIO.read(getClass().getResourceAsStream("/end_game.png"));
                gr.drawImage(gameOver, 0, 0, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // отвечает за передвижение текущей фигуры и проверяет его возможность, а именно:
    // не произойдёт ли столкновения с границами доски или упавшими фигурами
    private boolean tryMove(Shape shape, int newX, int newY)
    {
        for (int i = 0; i < MAX_SIZE; ++i) {
            int x = newX + shape.getX(i);
            int y = newY - shape.getY(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) return false;
            if (shapeBelow(x, y) != ShapeForm.EmptyShape) return false;
        }

        currentShape = shape;
        curX = newX;
        curY = newY;
        repaint();

        return true;
    }

    // по нажатию "вниз" спускает текущую фигуру по оси Y на сколько это возможно
    private void throwDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(currentShape, curX, newY - 1)) break;
            --newY;
        }
        shapeFalled();
    }

    // проверяет какая фигура снизу по курсу
    private ShapeForm shapeBelow(int x, int y)
    {
        return forms[y * BOARD_WIDTH + x];
    }

    // удаляет успешно заполненные строки, начисляет очки
    private void dropFullLines()
    {
        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {
                if (shapeBelow(j, i) == ShapeForm.EmptyShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {
                        forms[k * BOARD_WIDTH + j] = shapeBelow(j, k + 1);
                    }
                }
                score += 30;
                lb.setText("Score: " + score);
                isFell = true;
            }
        }
    }
}