package net.bitbylogic.utils.smallcaps;

public class SmallCapsConverter {

    /**
     * Converts the input string to a representation where each uppercase Latin letter
     * is replaced by its corresponding small capital letter.
     *
     * <p>This method transforms the given string by converting it to uppercase and
     * then replacing each uppercase Latin letter (A-Z) with its corresponding small
     * capital letter. The letter 'X' is treated specially and is replaced with its
     * corresponding Latin small letter 'x'.</p>
     *
     * <p><b>Example:</b> The string 'Vaulted' would be converted to 'ᴠᴀᴜʟᴛᴇᴅ'.</p>
     *
     * @param string the input string to be converted
     * @return the converted string with small capital letters
     */
    public static String convert(String string) {
        StringBuilder builder = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            char prevChar = (i > 0) ? string.charAt(i - 1) : '\0';

            if (prevChar == '§' || prevChar == '&') {
                builder.append(c);
                continue;
            }

            switch (Character.toUpperCase(c)) {
                case 'A' -> builder.append('ᴀ');
                case 'B' -> builder.append('ʙ');
                case 'C' -> builder.append('ᴄ');
                case 'D' -> builder.append('ᴅ');
                case 'E' -> builder.append('ᴇ');
                case 'F' -> builder.append('ꜰ');
                case 'G' -> builder.append('ɢ');
                case 'H' -> builder.append('ʜ');
                case 'I' -> builder.append('ɪ');
                case 'J' -> builder.append('ᴊ');
                case 'K' -> builder.append('ᴋ');
                case 'L' -> builder.append('ʟ');
                case 'M' -> builder.append('ᴍ');
                case 'N' -> builder.append('ɴ');
                case 'O' -> builder.append('ᴏ');
                case 'P' -> builder.append('ᴘ');
                case 'Q' -> builder.append('ꞯ');
                case 'R' -> builder.append('ʀ');
                case 'S' -> builder.append('ꜱ');
                case 'T' -> builder.append('ᴛ');
                case 'U' -> builder.append('ᴜ');
                case 'V' -> builder.append('ᴠ');
                case 'W' -> builder.append('ᴡ');
                case 'X' -> builder.append('x');
                case 'Y' -> builder.append('ʏ');
                case 'Z' -> builder.append('ᴢ');
                default -> builder.append(c);
            }
        }

        return builder.toString();
    }

}
