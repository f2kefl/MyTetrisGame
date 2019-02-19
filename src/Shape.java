/*
* Класс-фигура. Представляет собой форму и соответствующий ей двумерный массив с координатами Х и У.
* */

class Shape
{
    ShapeForm currentForm; // текущая форма
    private int[][] coordinates; // массив с координатами квадратов формы

    // в конструкторе происходит инициализация массива координат и фигуре задаётся пустая форма
    Shape()
    {
        coordinates = new int[Tetris.MAX_SIZE][2];
        setShape();
    }

    // метод задаёт форму для фигуры, меняя текущий массив координат формы на нужный.
    private void setShape()
    {
        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            System.arraycopy(ShapeForm.EmptyShape.coordinates[i], 0, coordinates[i], 0, 2);
        }

        currentForm = ShapeForm.EmptyShape;
    }

    // метод генерирует случайную форму, кроме пустой
    void getRandomForm()
    {
        int r = (int) (1 + Math.random() * 7);
        ShapeForm[] shapes = ShapeForm.values();
        coordinates = shapes[r].coordinates;
        currentForm = shapes[r];
    }

    // метод вращает фигуру вокруг оси по направлению часовой стрелки.
    Shape turn()
    {
        Shape result = new Shape();
        result.currentForm = currentForm;

        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            result.setX(i, -getY(i));
            result.setY(i, getX(i));
        }

        return result;
    }

    // метод находит верхний квадрат фигуры для правильного отображения при её создании.
    int minY()
    {
        int m = coordinates[0][1];

        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            m = Math.min(m, coordinates[i][1]);
        }

        return m;
    }

    // методы задают/получают координаты Х и У выбранного квадрата.
    private void setX(int index, int x)
    {
        coordinates[index][0] = x;
    }

    private void setY(int index, int y)
    {
        coordinates[index][1] = y;
    }

    int getX(int index)
    {
        return coordinates[index][0];
    }

    int getY(int index)
    {
        return coordinates[index][1];
    }
}