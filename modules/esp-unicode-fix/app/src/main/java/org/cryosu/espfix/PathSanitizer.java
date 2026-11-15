package org.cryosu.espfix;

/** Membuang karakter tak terlihat yang dipakai untuk melewati filter path. */
final class PathSanitizer {
    private PathSanitizer() {}

    static boolean isInvisible(char c) {
        // Seluruh kategori Cf (format): ZWSP/ZWNJ/ZWJ, U+200E/F, U+2060, U+FEFF, soft hyphen U+00AD,
        // kontrol bidi, dst. Ditambah dua karakter non-spasi yang juga tak tampak.
        return Character.getType(c) == Character.FORMAT || c == '͏' || c == '᠎';
    }

    /** Mengembalikan teks tanpa karakter tak terlihat; instans yang sama bila tidak ada yang dibuang. */
    static String strip(String s) {
        if (s == null) return null;
        StringBuilder sb = null;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isInvisible(c)) {
                if (sb == null) sb = new StringBuilder(s.length()).append(s, 0, i);
            } else if (sb != null) {
                sb.append(c);
            }
        }
        return sb == null ? s : sb.toString();
    }
}
