package ru.nsu.bolotov.util;

public class UtilConsts {
    public static final class DimensionConsts {
        public static final int MINIMAL_WEIGHT = 640;
        public static final int MINIMAL_HEIGHT = 480;
        public static final int STANDARD_DIALOG_SIZE = 220;

        private DimensionConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class ButtonNameConsts {
        public static final String ABOUT_BUTTON = "О программе";

        private ButtonNameConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class ProgramConfigurationConsts {
        public static final int THREADS_NUMBERS = 8;

        private ProgramConfigurationConsts() {
            throw new IllegalStateException(StringConsts.INSTANTIATION_MESSAGE);
        }
    }

    public static final class StringConsts {
        public static final String INSTANTIATION_MESSAGE = "Instantiation of util class";
        public static final String APPLICATION_TITLE = "Custom Filter";
        public static final String ABOUT_PROGRAM_TEXT = """
                Информация о программе Custom Filter
                Программа выполнена Болотовым Кириллом
                март 2024
                
                Доступные инструменты:
                TODO
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
