import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/* Create by Nikita 10.06.2016
   Игра тетрис, главный класс (механика и точка запуска программы)
 */
class MyFrame extends JFrame
{
    private MyFrame()
    {
        Tetris tetris = new Tetris();
        add(tetris);
        setTitle("Game \"Tetris\"");
        setBounds(0, 0, 380, 340);
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
    // константы
    static final int MAX_SIZE = 6; // максимальное кол-во точек в фигуре
    private static final int SQUARE_SIZE = 15; // размер квадрата (пикселей)
    private static final int BOARD_WIDTH = 15; // ширина поля (квадратов)
    private static final int BOARD_HEIGHT = 20; // длина поля (квадратов)
    // переменные
    private Timer tmDraw; // время обновления доски (мс)
    private boolean isFell = false;// упала ли фигура
    private boolean isStarted = false; // падает ли фигура
    private boolean gameOver = false; // закончилась ли игра
    private int curX = 0; // положение текущей фигуры на доске
    private int curY = 0;
    private int score = 0; // считается как число удалённых c поля квадратов (30 за линию)
    private Shape currentShape; // падающая фигура
    private ShapeForm[] forms; //массив фигур, упавших на доску
    private JLabel scoreLabel;
    JLabel gameOverLabel;

    Tetris()
    {
        tmDraw = new Timer(300, e -> {
            if (isFell) {
                isFell = false;
                newShape();
            }
            else if(!gameOver) oneLineDown();
            if (gameOver){
                gameOverLabel.setText("GAME OVER");
            }
        });
        setFocusable(true);
        setLayout(null);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.DARK_GRAY);
        scoreLabel.setFont(new Font("comic sans MS", Font.PLAIN, 16));
        scoreLabel.setBounds(243, 130, 130, 40);
        add(scoreLabel);

        JButton btn1 = new JButton("New Game");
        btn1.setForeground(new Color(47, 126, 154));
        btn1.setFont(new Font("comic sans MS", Font.PLAIN, 14));
        btn1.setBounds(243, 10, 105, 30);
        btn1.addActionListener(arg0 -> start());
        add(btn1);

        JButton btn2 = new JButton("Exit");
        btn2.setForeground(new Color(232, 95, 76));
        btn2.setFont(new Font("comic sans MS", Font.PLAIN, 14));
        btn2.setBounds(243, 260, 105, 30);
        btn2.addActionListener(arg0 -> System.exit(0));
        add(btn2);

        gameOverLabel = new JLabel("");
        gameOverLabel.setForeground(new Color(190, 0, 12));
        gameOverLabel.setFont(new Font("comic sans MS", Font.BOLD, 20));
        gameOverLabel.setBounds(232, 90, 130, 40);
        add(gameOverLabel);

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
    private void start()
    {
        grabFocus();
        gameOver = false;
        isStarted = true;
        isFell = false;
        score = 0;
        gameOverLabel.setText("");
        scoreLabel.setText("Score: " + score);
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
    private void newShape()
    {
        currentShape.getRandomForm();
        curX = BOARD_WIDTH / 2;
        curY = BOARD_HEIGHT + currentShape.minY();

        if (!tryMove(currentShape, curX, curY-1)) {
            isStarted = false;
            gameOver = true;
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
    // рисует доску и соединяет квадраты в фигуры
    @Override
    public void paint(Graphics gr)
    {
        super.paint(gr);

        gr.setColor(new Color(47, 126, 154));
        gr.fillRect(0, 0, 226, 301);

        gr.setColor(new Color(155, 155, 155));
        for (int i = 0; i <= BOARD_WIDTH; i++) // доска BOARD_HEIGHT * BOARD_WIDTH площадью, клетка 10 на 10
        {
            gr.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE, 301);
            for (int j = 0; j <= BOARD_HEIGHT; j++) {
                gr.drawLine(0, j * SQUARE_SIZE, 225, j * SQUARE_SIZE);
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

    // по нажатию "вниз" спускает текущую фигуру по оси Y, пока она не столкнется с уже лежащей либо с дном колодца
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
                scoreLabel.setText("Score: " + score);
                isFell = true;
            }
        }
    }
}