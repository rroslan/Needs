package biz.eastservices.suara.Helper;

import android.content.Context;

import de.psdev.licensesdialog.licenses.License;

public class NeedsTerm extends License {

    private static final long serialVersionUID = 4854000061990891449L;

    @Override
    public String getName() {
        return "Needs Term 1.0";
    }

    @Override
    public String readSummaryTextFromResources(final Context context) {
        return getContent(context, de.psdev.licensesdialog.R.raw.asl_20_summary);
    }

    @Override
    public String readFullTextFromResources(final Context context) {
        return getContent(context, de.psdev.licensesdialog.R.raw.asl_20_full);
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getUrl() {
        return "https://www.apache.org/licenses/LICENSE-2.0.txt";
    }


}
