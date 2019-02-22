import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/* Create by Nikita 10.06.2016
   Игра тетрис, главный файл (механика и точка запуска программы).
 */
class MyFrame extends JFrame
{   // создание главного окна
    private MyFrame()
    {
        Tetris tetris = new Tetris(); // инициализация
        add(tetris); // добавление панели (о ней ниже)
        setTitle("Game \"Tetris\""); // название
        setBounds(0, 0, 380, 340); // расположение на панели и размеры
        // при нажатии на "крестик" программа закрывается
        // по умолчанию в JAVA закрывается только окно, сама программа продолжает работать
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true); // делаем окно видимым, по умолчанию в JAVA false
    }
    // точка запуска программы
    public static void main(String[] args)
    {
        MyFrame myFrame = new MyFrame();
        myFrame.setLocationRelativeTo(null); // чтобы открывалось в центре экрана
        myFrame.setResizable(false); // чтобы нельзя было менять размер окна
    }
}
    // класс панели, на которой все размещено
public class Tetris extends JPanel
{
    // константы
    static final int MAX_SIZE = 6; // максимальное кол-во квадратов в фигуре
    private static final int SQUARE_SIZE = 15; // размер квадрата (пикселей)
    private static final int BOARD_WIDTH = 15; // ширина поля (квадратов)
    private static final int BOARD_HEIGHT = 20; // длина поля (квадратов)
    private static final int GAME_SPEED = 250; // скорость падения фигур (миллисекунд). Можно менять, чтобы регулировать сложность
    // переменные
    private Timer tmDraw; // обновляет доску
    private boolean isFell = false; // упала ли фигура
    private boolean isStarted = false; // началась ли игра
    private boolean gameOver = false; // закончилась ли игра
    private int curX = 0; // положение текущей фигуры на доске, по горизонтали
    private int curY = 0; // положение текущей фигуры на доске, по вертикали
    private int score = 0; // считается как число удалённых c поля квадратов (15 за линию)
    private Shape currentShape; // падающая фигура
    private ShapeForm[] forms; // массив форм фигур
    private JLabel scoreLabel; // набранные очки
    private JLabel gameOverLabel; // текст появляется, если игра закончена

    Tetris() // главный класс, создаются все элементы окна, запускается процесс отрисовки
    {
        // Таймер — что-то вроде потока (Thread), но попроще
        // обновляет доску каждое заданное число миллисекунд (GAME_SPEED)
        tmDraw = new Timer(GAME_SPEED, e -> {
            if (isFell) { // следим, падает ли фигура
                isFell = false; // если уже упала, то создается новая
                newShape();
            }
            else if(!gameOver) oneLineDown(); // если еще падает и игра не закончилась, то опускаем ее на 1 строку каждые GAME_SPEED мс

            if (gameOver){ // если игра закончилась (фигурам некуда падать), то выводим пользователю надпись GAME OVER
                gameOverLabel.setText("GAME OVER");
            }
        });
        setFocusable(true); // устанавливает фокус на эту панель, в противном случае фокус ловят кнопки
        setLayout(null); // NullLayout — это метод размещения объектов на панели, при котором я пишу конкретные координаты по осям X/Y

        // запись очков игрока: инициализация, цвет, шрифт, расположение на панели, добавление на панель (по порядку)
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.DARK_GRAY);
        scoreLabel.setFont(new Font("comic sans MS", Font.PLAIN, 16));
        scoreLabel.setBounds(243, 130, 130, 40);
        add(scoreLabel);
        // кнопка новой игры: инициализация, цвет, шрифт, расположение на панели, действие при нажатии, добавление на панель (по порядку)
        JButton btn1 = new JButton("New Game");
        btn1.setForeground(new Color(47, 126, 154));
        btn1.setFont(new Font("comic sans MS", Font.PLAIN, 14));
        btn1.setBounds(243, 10, 105, 30);
        btn1.addActionListener(arg0 -> start());
        add(btn1);
        // кнопка выхода: инициализация, цвет, шрифт, расположение на панели, действие при нажатии, добавление на панель (по порядку)
        JButton btn2 = new JButton("Exit");
        btn2.setForeground(new Color(232, 95, 76));
        btn2.setFont(new Font("comic sans MS", Font.PLAIN, 14));
        btn2.setBounds(243, 260, 105, 30);
        btn2.addActionListener(arg0 -> System.exit(0));
        add(btn2);
        // надпись GAME OVER: инициализация, цвет, шрифт, расположение на панели, добавление на панель (по порядку)
        gameOverLabel = new JLabel("");
        gameOverLabel.setForeground(new Color(190, 0, 12));
        gameOverLabel.setFont(new Font("comic sans MS", Font.BOLD, 20));
        gameOverLabel.setBounds(232, 90, 130, 40);
        add(gameOverLabel);

        currentShape = new Shape(); // текущая падающая фигура

        forms = new ShapeForm[BOARD_WIDTH * BOARD_HEIGHT]; // создание клеточного поля

        start(); // вызываем метод начала игры (описание ниже)

        addKeyListener(new KeyAdapter() // действия для кнопок
        {
            @Override
            public void keyPressed(KeyEvent key)
            {
                if (!isStarted || currentShape.currentForm == ShapeForm.EmptyShape) return; // проверяем, идет ли игра

                int keyCode = key.getKeyCode(); // получаем код нажатой кнопки

                switch (keyCode) {
                    case KeyEvent.VK_LEFT: // стрелка влево, фигура двигается влево на 1 квадрат
                        tryMove(currentShape, curX - 1, curY);
                        break;
                    case KeyEvent.VK_RIGHT: // стрелка вправо, фигура двигается вправо на 1 квадрат
                        tryMove(currentShape, curX + 1, curY);
                        break;
                    case KeyEvent.VK_DOWN: // стрелка вниз, фигура падает моментально
                        throwDown();
                        break;
                    case KeyEvent.VK_UP: // стрелка вверх, вращает фигуру по часовой стрелке
                        tryMove(currentShape.turn(), curX, curY);
                        break;
                }
            }
        });
    }

    // начало игры
    private void start()
    {
        grabFocus(); // фокус на доску
        gameOver = false; // возвращаем переменные в стартовые значения
        isStarted = true;
        isFell = false;
        score = 0;
        gameOverLabel.setText("");
        scoreLabel.setText("Score: " + score);
        clearBoard(); // очищаем доску
        newShape(); // создаем новую фигуру
        tmDraw.start(); // запуск таймера
    }

    // очищает доску (заполняет пустыми фигурами)
    private void clearBoard()
    {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) { // проходимся циклом по каждой точке поля, каждый заполненный квадрат делаем пустым
            forms[i] = ShapeForm.EmptyShape;
        }
    }

    // создаёт новую фигуру и помещает в позицию начала падения
    private void newShape()
    {
        currentShape.getRandomForm(); // получаем случайную фигуру
        curX = BOARD_WIDTH / 2; // координаты начала движения
        curY = BOARD_HEIGHT + currentShape.minY();

        if (!tryMove(currentShape, curX, curY-1)) { // проверяем, может ли фигура продолжать движение
            isStarted = false; // если нет, то игра закончилась
            gameOver = true;
        }
    }

    // проверяет есть ли куда падать текущей фигуре и заставляет падать на 1 квадрат за каждый вызов метода
    private void oneLineDown()
    {
        if (!tryMove(currentShape, curX, curY - 1)) shapeFalled();
    }

    // вызывается, если фигура упала
    private void shapeFalled()
    {
        for (int i = 0; i < Tetris.MAX_SIZE; i++) { // вычисляем где находятся точки ближайших упавших фигур, помещаем рядом
            int x = curX + currentShape.getX(i);
            int y = curY - currentShape.getY(i);
            forms[y * BOARD_WIDTH + x] = currentShape.currentForm;
        }

        dropFullLines(); // удаляем пустые линии (описание метода ниже)

        if (!isFell) { // если фигура упала, создаем новую
            newShape();
        }
    }

    // отрисовывает квадраты для форм
    private void drawSquare(Graphics gr, int x, int y)
    {
        gr.setColor(new Color(232, 95, 76)); // цвет
        gr.fillRect(x + 1, y + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2); // рисуем прямоугольник, имитируем объемность
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

        gr.setColor(new Color(47, 126, 154)); // цвет доски
        gr.fillRect(0, 0, 226, 301); // размер доски

        gr.setColor(new Color(155, 155, 155));
        for (int i = 0; i <= BOARD_WIDTH; i++) // рисуем линии на доске
        {
            gr.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE, 301);
            for (int j = 0; j <= BOARD_HEIGHT; j++) {
                gr.drawLine(0, j * SQUARE_SIZE, 225, j * SQUARE_SIZE);
            }
        }

        for (int i = 0; i < BOARD_HEIGHT; i++) { // отрисовка упавших фигур
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                ShapeForm shape = getSquare(j, BOARD_HEIGHT - i - 1);

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
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) return false; // проверяем нет ли рядом стен поля
            if (getSquare(x, y) != ShapeForm.EmptyShape) return false; // проверяем, нет ли внизу уже упавших фигур
        }

        currentShape = shape;
        curX = newX;
        curY = newY;
        repaint(); // перерисовка доски после каждого вызова метода

        return true;
    }

    // по нажатию "вниз" спускает текущую фигуру по оси Y, пока она не столкнется с уже лежащей либо с дном колодца
    private void throwDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(currentShape, curX, newY - 1)) break; // проверяем сколько квадратов может преодолеть фигура до первого припятствия
            --newY; // опускаем фигуру на это количество квадратов вниз
        }
        shapeFalled();
    }

    // проверяет заполнен ли квадрат
    private ShapeForm getSquare(int x, int y)
    {
        return forms[y * BOARD_WIDTH + x];
    }

    // удаляет успешно заполненные строки, начисляет очки
    private void dropFullLines()
    {
        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {
                if (getSquare(j, i) == ShapeForm.EmptyShape) { // проверяем каждую горизонтальную линию квадратов, если хоть один ее квадрат пустой — линия не полная
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) { // если линия полная, то очищаем все заполненные квадраты на ней и сдвигаем верхние линии вниз
                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {
                        forms[k * BOARD_WIDTH + j] = getSquare(j, k + 1);
                    }
                }
                score += BOARD_WIDTH; // прибавляем 15 очков игроку
                scoreLabel.setText("Score: " + score);
                isFell = true;
            }
        }
    }
}