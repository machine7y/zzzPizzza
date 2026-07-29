<package>

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun atLeastT() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
