/*
* �����-������. ������������ ����� ����� � ��������������� �� ��������� ������ � ������������ � � �.
* */

public class Shape
{
    public ShapeForm currentForm; // ������� �����
    public int[][] coordinates; // ������ � ������������ ��������� �����

    // � ������������ ���������� ������������� ������� ��������� � ������ ������� ������ �����
    public Shape()
    {
        coordinates = new int[Tetris.MAX_SIZE][2];
        setShape(ShapeForm.EmptyShape);
    }

    // ����� ����� ����� ��� ������, ����� ������� ������ ��������� ����� �� ������.
    public void setShape(ShapeForm shape)
    {
        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            System.arraycopy(shape.coordinates[i], 0, coordinates[i], 0, 2);
        }

        currentForm = shape;
    }

    // ����� ���������� ��������� �����, ����� ������
    public void getRandomForm()
    {
        int r = (int) (1 + Math.random() * 7);
        ShapeForm[] shapes = ShapeForm.values();
        coordinates = shapes[r].coordinates;
        currentForm = shapes[r];
    }

    // ����� ������� ������ ������ ��� �� ����������� ������� �������.
    public Shape turn()
    {
        Shape result = new Shape();
        result.currentForm = currentForm;

        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            result.setX(i, -getY(i));
            result.setY(i, getX(i));
        }

        return result;
    }

    // ����� ������� ������� ������� ������ ��� ����������� ����������� ��� � ��������.
    public int minY()
    {
        int m = coordinates[0][1];

        for (int i = 0; i < Tetris.MAX_SIZE; i++) {
            m = Math.min(m, coordinates[i][1]);
        }

        return m;
    }

    // ������ ������/�������� ���������� � � � ���������� ��������.
    private void setX(int index, int x)
    {
        coordinates[index][0] = x;
    }

    private void setY(int index, int y)
    {
        coordinates[index][1] = y;
    }

    public int getX(int index)
    {
        return coordinates[index][0];
    }

    public int getY(int index)
    {
        return coordinates[index][1];
    }
}