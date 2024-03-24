package ru.nsu.bolotov.util;

public class UtilConsts {
    public static final class DimensionConsts {
        public static final int MINIMAL_WEIGHT = 640;
        public static final int MINIMAL_HEIGHT = 480;
        public static final int IMAGE_PANEL_SIZE = 600;
        public static final int STANDARD_DIALOG_SIZE = 220;
        public static final int BUTTON_WITH_TEXT_WIDTH = 120;
        public static final int CONFIRM_BUTTON_WIDTH = 60;
        public static final int STANDARD_BUTTON_SIZE = 40;
        public static final int CHOOSE_DIALOG_HEIGHT = 90;

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
                1. Перевод изображения в ЧБ цвета
                2. Негативный фильтр
                3. Сглаживающий фильтр (по Гауссу для матриц размера 3 или 5 / медианный для больших размеров)
                4. Повышение резкости изображения
                5. Фильтр тиснения
                6. Гамма-коррекция изображения
                7. Выделение границ изображения (по Робертсу или по Собелю)
                8. Дизеринг по алгоритму Флойда-Стейнберга
                9. Упорядоченный дизеринг изображения
                10. Аквареализация для изображения
                11. Фильтр поворота на произвольный угол
                12. Ретро-фильтр для изображения
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
