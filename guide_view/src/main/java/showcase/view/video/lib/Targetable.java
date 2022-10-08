package showcase.view.video.lib;

import android.graphics.Path;
import android.graphics.RectF;

public interface Targetable {
    Path guidePath();

    RectF boundingRect();
}
