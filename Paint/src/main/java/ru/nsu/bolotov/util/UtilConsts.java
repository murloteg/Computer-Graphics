package ru.nsu.bolotov.util;

import java.awt.*;

public final class UtilConsts {
    public static final class DimensionConsts {
        public static final int MIN_WINDOW_WIDTH = 640;
        public static final int PREFERRED_WINDOW_WIDTH = 800;
        public static final int MIN_WINDOW_HEIGHT = 480;
        public static final int STANDARD_DIALOG_SIZE = 220;
        public static final int CHOOSE_DIALOG_HEIGHT = 90;
        public static final int POLYGON_DIALOG_HEIGHT = 90;
        public static final int CONFIRM_BUTTON_WIDTH = 60;
        public static final int STANDARD_BUTTON_SIZE = 40;
        public static final int CANVAS_SIZE_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
        public static final int CANVAS_SIZE_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

        private DimensionConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class PolygonConsts {
        public static final int DEFAULT_POLYGON_VERTICES = 4;
        public static final int DEFAULT_POLYGON_RADIUS = 45;
        public static final int DEFAULT_POLYGON_ROTATION = 0;

        private PolygonConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class ButtonNameConsts {
        public static final String ABOUT_BUTTON = "О программе";

        private ButtonNameConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class StringConsts {
        public static final String INSTANTIATION_MESSAGE = "Instantiation of util class";
        public static final String APPLICATION_TITLE = "Custom Paint";
        public static final String ABOUT_PROGRAM_TEXT = """
                Информация о программе Custom Paint
                Программа выполнена Болотовым Кириллом
                февраль 2024
                
                Доступные инструменты:
                1. Инструмент "Кисть": позволяет непрерывно рисовать на холсте
                2. Инструмент "Линия": позволяет рисовать линии на холсте
                3. Инструмент "Заливка": позволяет залить 4-связную область цветом
                4. Инструмент "Стереть": позволяет очистить холст
                5. Инструмент "Многоугольник": позволяет рисовать правильные многоугольники на холсте
                6. Инструмент "Звезда": позволяет рисовать правильные звезды на холсте
                7. Инструмент "Настройки": позволяет конфигурировать параметры для Линии и Полигонов 
                """;
        public static final String ERROR_TITLE = "Произошла ошибка";
        public static final String INCORRECT_FILE_EXTENSION_MESSAGE = "Указано некорректное расширение файла!";

        private StringConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    private UtilConsts() {
        throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
    }
}
