package dev.polv.polcinematics.cinematic.compositions.helpers;


public class CompositionInfo {

    private String[] info;

    public CompositionInfo() {
        this.info = new String[0];
    }

    public void addInfo(String key, String value) {
        String[] newInfo = new String[info.length + 2];
        System.arraycopy(info, 0, newInfo, 0, info.length);
        newInfo[newInfo.length - 2] = key;
        newInfo[newInfo.length - 1] = value;
        this.info = newInfo;
    }

    private void check() {
        if (info.length % 2 != 0) {
            // cut array
            String[] newInfo = new String[info.length - 1];
            System.arraycopy(info, 0, newInfo, 0, newInfo.length);
            this.info = newInfo;
        }
    }

    /**
     * Add raw info parameters.
     * @param rawinfo The first parameter will be the key, the second the value, the third the key, the fourth the value, etc.
     */
    public void addInfo(Object... rawinfo) {
        String[] info = new String[rawinfo.length];
        for (int i = 0; i < info.length; i++) {
            info[i] = String.valueOf(rawinfo[i]);
        }

        String[] newInfo = new String[this.info.length + info.length];
        System.arraycopy(this.info, 0, newInfo, 0, this.info.length);
        System.arraycopy(info, 0, newInfo, this.info.length, info.length);
        this.info = newInfo;
    }

    /**
     * @return The info parameters. The first parameter will be the key, the second the value, the third the key, the fourth the value, etc.
     */
    public String[] getInfo() {
        check();
        return info;
    }

}
