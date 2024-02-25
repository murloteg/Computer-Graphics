package ru.nsu.bolotov.util;

import java.awt.*;

public final class UtilConsts {
    public static final class DimensionConsts {
        public static final int MIN_WINDOW_WIDTH = 640;
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
        public static final String ABOUT_PROGRAM_TEXT = "<html>Информация о программе Paint<br>Программа выполнена студентом Болотовым К.Ю.<br>2024</html>";

        private StringConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    private UtilConsts() {
        throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
    }
}
