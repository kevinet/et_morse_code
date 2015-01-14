package cn.wecook.et.morsecode;

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

/**
 * 摩斯电码
 *
 * @author kevin
 * @version v1.0
 * @since 2015-1/12/15
 */
public class MorseCodeUtils {

    /**
     * 分割符号
     */
    public static final String SPLIT = ",";

    /**
     * 每个字符之间的间隔时间
     */
    public static long WAIT_TIME = 1000;

    /**
     * 长符号持续时间
     */
    public static long LONG_TIME = 700;

    /**
     * 短符号持续时间
     */
    public static long SHORT_TIME = 100;

    /**
     * 符号之间的间隔时间
     */
    public static long SPLIT_TIME = 500;

    /**
     * 符号编码对应表
     */
    private static final String[] CODES = {
            ".-",//A
            "-...",//B
            "-.-.",//C
            "-..",//D
            ".",//E
            "..-.",//F
            "--.",//G
            "....",//H
            "..",//I
            ".---",//J
            "-.-",//K
            ".-..",//L
            "--",//M
            "-.",//N
            "---",//O
            ".--.",//P
            "--.-",//Q
            ".-.",//R
            "...",//S
            "-",//T
            "..-",//U
            "...-",//V
            ".--",//W
            "-..-",//X
            "-.--",//Y
            "--..",//Z
            "-----",//0
            ".----",//1
            "..---",//2
            "...--",//3
            "....-",//4
            ".....",//5
            "-....",//6
            "--...",//7
            "---..",//8
            "----.",//9
            "..--..",//?
            "-..-.",// /
            "-.--.-",// ()
            "-....-",// -
            ".-.-.-",// .
    };
    /**
     * 摩斯码打点器
     */
    private static MorseCodeGenerator mGenerator = null;

    /**
     * 转换成摩斯码
     *
     * @param plaintext
     * @return
     */
    public static String translateToCode(String plaintext) {
        final String upText = plaintext.toUpperCase();
        String code = "";
        for (int i = 0; i < upText.length(); i++) {
            char c = upText.charAt(i);
            if (c >= 65 && c <= 90) {//A-Z
                code += CODES[c - 65];
            } else if (c >= 48 && c <= 57) {//0-9
                code += CODES[c - 48 + 26];
            } else if (c == '?') {
                code += CODES[36];
            } else if (c == '/') {
                code += CODES[37];
            } else if (c == '-') {
                code += CODES[39];
            } else if (c == '.') {
                code += CODES[40];
            }

            if (i != upText.length() - 1) {
                code += SPLIT;
            }
        }

        return code;
    }

    /**
     * 转换成可识别文字
     *
     * @param code
     * @return
     */
    public static String translateToHuman(String code) {
        StringBuffer text = new StringBuffer();
        String[] subCodes = code.split(SPLIT);
        for (String subcode : subCodes) {
            for (int i = 0; i < CODES.length; i++) {
                if (subcode.equals(CODES[i])) {
                    if (i < 26) {
                        text.append((char) (i + 65));
                    } else if (i >= 26 && i < 36) {
                        text.append((char) (i - 26 + 48));
                    } else if (i == 36) {
                        text.append('?');
                    } else if (i == 37) {
                        text.append('/');
                    } else if (i == 39) {
                        text.append('-');
                    } else if (i == 40) {
                        text.append('.');
                    }
                    break;
                }
            }
        }

        return text.toString();
    }

    /**
     * 摩斯码震动
     *
     * @param context
     * @param text
     */
    public static void vibrateMorseCode(Context context, String text) {
        String code = translateToCode(text);

        String[] cells = code.split(SPLIT);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
        vibrator.vibrate(getVibrateTime(cells), -1);

    }

    /**
     * 对一组符号串获取对应时长
     *
     * @param cells
     * @return
     */
    private static long[] getVibrateTime(String[] cells) {

        int count = 0;
        for (String cell : cells) {
            count += cell.length() * 2;
        }

        long[] morseCodeTime = new long[count];

        int offset = 0;
        for (String cell : cells) {

            for (int i = 0; i < cell.length(); i++) {
                char c = cell.charAt(i);
                if (i == 0 && offset != 0) {
                    morseCodeTime[i * 2 + offset] = WAIT_TIME;
                } else {
                    morseCodeTime[i * 2 + offset] = SPLIT_TIME;
                }

                if (c == '.') {
                    morseCodeTime[i * 2 + offset + 1] = SHORT_TIME;
                } else if (c == '-') {
                    morseCodeTime[i * 2 + offset + 1] = LONG_TIME;
                }

            }
            offset += cell.length() * 2;

        }
        return morseCodeTime;
    }

    public static void setLongTime(long longTime) {
        LONG_TIME = longTime;
    }

    public static void setShortTime(long shortTime) {
        SHORT_TIME = shortTime;
    }

    public static void setSplitTime(long splitTime) {
        SPLIT_TIME = splitTime;
    }

    public static void setWaitTime(long waitTime) {
        WAIT_TIME = waitTime;
    }

    /**
     * 在View上执行摩斯码打点
     *
     * @param context
     * @param view
     * @param touchCallback
     */
    public static void morseCodeOnView(final Context context, View view, final View.OnTouchListener touchCallback) {
        if (mGenerator == null) {
            mGenerator = new MorseCodeUtils.MorseCodeGenerator();
        }

        mGenerator.start();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mGenerator.clickDown(context);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mGenerator.clickUp(context);
                        break;
                }
                if (touchCallback != null) {
                    touchCallback.onTouch(v, event);
                }
                return true;
            }
        });
    }

    /**
     * 获得当前摩斯码打点器的摩斯码
     *
     * @return
     */
    public static String getMorseCode() {
        return mGenerator != null ? mGenerator.getCode() : "";
    }

    /**
     * 摩斯电码发点器
     */
    public static class MorseCodeGenerator {

        private String code;

        private long startTime;

        public void start() {
            code = "";
            startTime = 0;
        }

        public String getCode() {
            return code;
        }

        /**
         * 点击按下
         *
         * @param context
         */
        public void clickDown(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(3000);
            long current = System.currentTimeMillis();
            long time = current - startTime;
            if (time >= WAIT_TIME && startTime != 0) {
                code += SPLIT;
            }
            startTime = System.currentTimeMillis();
        }

        /**
         * 点击抬起
         *
         * @param context
         */
        public void clickUp(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.cancel();
            long current = System.currentTimeMillis();

            long time = current - startTime;
            if (time > 0 && time <= SHORT_TIME) {
                code += ".";
            } else if (time > SHORT_TIME && time <= LONG_TIME) {
                code += "-";
            }

            startTime = current;
        }
    }

}
