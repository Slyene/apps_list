package com.slyene.appslist.presentation.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.slyene.appslist.R
import com.slyene.appslist.domain.model.AppInfo

@Composable
fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(6.dp),
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
    ) {
        ListItem(
            headlineContent = { Text(text = app.name) },
            supportingContent = { Text(text = app.packageName) },
            leadingContent = {
                Image(
                    painter = rememberDrawablePainter(drawable = app.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = colorScheme.surfaceContainerLowest
            )
        )
    }
}

@Preview
@Composable
private fun AppListItemPreview() {
    val context = LocalContext.current

    AppListItem(
        app = AppInfo(
            name = "Test App",
            packageName = "com.test.app",
            versionName = "",
            sourceDir = "",
            icon = context.getDrawable(R.drawable.ic_launcher_foreground)!!,
        ),
        onClick = {},
    )
}