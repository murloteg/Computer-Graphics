package ru.nsu.bolotov.util;

public final class UtilConsts {
    public static final class DefaultApplicationParameters {
        public static final int DISTANCE_BETWEEN_AXIS_DASH_PX = 20;
        public static final int SUPPORT_POINT_RADIUS_PX = 20;
        public static final int DEFAULT_NUMBER_OF_BSPLINE_PART_SEGMENTS = 7;
        public static final int DEFAULT_NUMBER_OF_FORMING_LINES = 4;
        public static final int DEFAULT_CIRCLE_SMOOTHING_SEGMENTS = 10;
        public static final double DEFAULT_WIREFRAME_ZOOM_PARAMETER = 50.0;
        public static final double MINIMAL_WIREFRAME_ZOOM_PARAMETER = 25.0;
        public static final double MAXIMAL_WIREFRAME_ZOOM_PARAMETER = 300.0;
        public static final double DEFAULT_ROTATION_ANGLE_DEGREES = 6.0;

        private DefaultApplicationParameters() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class DimensionConsts {
        public static final int MINIMAL_WIDTH = 640;
        public static final int MINIMAL_HEIGHT = 480;
        public static final int STANDARD_DIALOG_SIZE = 220;
        public static final int STANDARD_BUTTON_SIZE = 35;

        private DimensionConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class StringConsts {
        public static final String BSPLINE_EDITOR_TITLE = "BSpline Editor";
        public static final String ABOUT_BUTTON = "О программе";
        public static final String ABOUT_PROGRAM_TEXT = """
                Информация о программе Custom Wireframe
                Программа выполнена Болотовым Кириллом
                апрель 2024
                
                Доступная функциональность:
                1. Редактор образующей, на основании которой можно отобразить трехмерную фигуру вращения
                2. Возможность добавлять и удалять опорные точки при построении B-сплайна
                3. Возможность поворота фигуры вращения с помощью мыши
                4. Возможность сбросить угол поворота при вращении фигуры к исходному
                """;
        public static final String ERROR_TITLE = "Произошла ошибка";
        public static final String INCORRECT_FILE_EXTENSION_MESSAGE = "Указано некорректное расширение файла!";
        public static final String INSTANTIATION_MESSAGE = "Instantiation of util class";

        private StringConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    private UtilConsts() {
        throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
    }
}
