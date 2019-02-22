Tetris. Лабораторная работа по заказу студентки.

Системные требования:
1. Компьютер с операционной системой Windows, Unix, Linux или Mac OS.
2. Java Runtime Edition версии не ниже 8.0.
3. Монитор.
4. Клавиатура, мышь.

Запуск игры осуществляется по двойному клику на MyTetrisGame.jar.

Принцип игры: 
Игроку дано прямоугольное поле-стакан 15 клеток в ширину и 20 в длину. С центральной верхней точки с начала игры начинают падать случайные геометрические фигуры разной формы*. Фигура продолжает падать пока не наткнется на другую, уже упавшую, либо на дно стакана. В полете игрок может поворачивать фигуру на 90° по направлению часовой стрелки, двигать ее по горизонтали и ускорять ее падение. Если при падении фигура полностью заполняет один или несколько горизонтальных рядов, то они исчезают, а все что выше них опускается вниз на количество клеток равное числу убранных рядов. Также за каждую убранную клетку игроку начисляется 1 очко (15 за ряд). Очки можно увидеть в окне приложения под кнопкой New Game. Изначально у любого игрока 0 очков (Score: 0).

Игра окончена, когда новой фигуре некуда падать. По окончании игры вы увидите красную надпись Game Over. Цель игрока — набрать как можно больше очков (заполнить как можно больше горизонтальных полей), не заполнив при этом стакан.

Управление осуществляется со стрелок клавиатуры:
1. Стрелка вверх поворачивает фигуру на 90° по направлению часовой стрелки.
2. Стрелка вправо перемещает фигуру по горизонтали на 1 клетку вправо.
3. Стрелка влево перемещает фигуру по горизонтали на 1 клетку влево.
4. Стрелка вниз мгновенно роняет фигуру вниз до ближайшего препятствия в виде другой фигуры или дна стакана.

Чтобы начать новую игру, нажмите кнопку New Game.
Чтобы выйти из игры, нажмите кнопку Exit либо крестик в правом верхнем углу окна.

Использованные средства:
1. Среда разработки IntelliJ IDEA 2018.3.
2. Java Development Kit version 11.0.2.
3. Графический интерфейс Java Swing.

22.02.2019, изменения:
1. Смена кодировки на UTF-8.
2. Уменьшение поля с 20х25 до 15х20.
3. Оптимизация модификаторов доступа к классам, методам и переменным.
4. Уменьшение интерфейса.
5. Исправлен баг, когда надпись "GAME OVER" по завершении игры не показывалась.
6. В классы добавлена подробная документация.
7. При компиляции теперь создается исполняемый файл MyTetrisGame.jar.
