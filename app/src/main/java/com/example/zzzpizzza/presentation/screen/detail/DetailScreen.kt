package com.example.zzzpizzza.presentation.screen.detail

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zzzpizzza.R
import com.example.zzzpizzza.presentation.components.StatusBarSpacer
import com.example.zzzpizzza.presentation.entity.PizzaUi
import com.example.zzzpizzza.presentation.entity.VariantUi
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnMinusClicked
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnPizzaSelected
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnPlusClicked
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnSizeClicked
import com.example.zzzpizzza.presentation.screen.detail.carousel.ImageCarousel
import com.example.zzzpizzza.presentation.theme.ActiveColor
import com.example.zzzpizzza.presentation.theme.HighlightColor
import com.example.zzzpizzza.presentation.theme.OnActiveColor
import com.example.zzzpizzza.presentation.theme.TextAndIconColor
import com.example.zzzpizzza.presentation.theme.White
import com.example.zzzpizzza.presentation.utils.createShadow
import com.example.zzzpizzza.presentation.utils.distributePoints
import com.example.zzzpizzza.presentation.utils.fullScreenHeightPx
import com.example.zzzpizzza.presentation.utils.fullScreenSizeDpSize
import com.example.zzzpizzza.presentation.utils.fullScreenWidthDp
import com.example.zzzpizzza.presentation.utils.fullScreenWidthPx
import com.example.zzzpizzza.presentation.utils.toDp
import com.example.zzzpizzza.presentation.utils.toPx
import kotlin.math.sqrt

private const val RADIUS_RATIO_TO_SCREEN_HEIGHT = 0.37f
private const val CENTER_POINT_RATIO_TO_SCREEN_HEIGHT = 0.27f
private const val BACKGROUND_INITIAL_RADIUS_SCALE = 2f
private const val BACKGROUND_ZOOM_RADIUS_SCALE = 2f
private const val BANANA_RADIUS_RATIO_TO_BACKGROUND_RADIUS = 0.857f
private const val HEADER_HIDDEN_OFFSET_IN_BUTTON_SIZES = 2
private const val ANIM_SCREEN_RADIUS_SPRING_DUMP_RATIO = 0.7f
private const val ANIM_SIZE_SPRING_DUMP_RATIO = 0.7f
private const val ANIM_TITLE_ANIMATION_DURATION_MILLIS = 300

private val HORIZONTAL_SCREEN_PADDING = 24.dp
private val BOTTOM_CONTENT_PADDING_TOP = 45.dp
private val BOTTOM_CONTENT_PADDING_BOTTOM = 57.dp
private val DESCRIPTION_PADDING_START = 40.dp
private val DESCRIPTION_PADDING_END = 21.dp
private val COUNTER_WIDTH = 143.dp
private val COUNTER_HEIGHT = 48.dp
private val ADD_BUTTON_MIN_WIDTH = 83.dp
private val ADD_BUTTON_MIN_HEIGHT = 48.dp
private val SIZE_BUTTON_SIZE = 48.dp
private val SIZE_BUTTON_SELECTED_SIZE = 52.dp
private val SIZE_BUTTON_SELECTED_BORDER = 2.dp
private val BANANA_WIDTH = 97.dp
private val BANANA_HEIGHT = 63.dp
private val HEADER_BUTTON_SIZE = 48.dp
private val HEADER_ZOOM_OFFSET = 260.dp
private val HEADER_TOP_SPACE = 18.dp
private val HEADER_PADDING_TOP = 36.dp
private val CENTER_IMAGE_CIRCLE_RADIUS = 65.dp

private val LARGE_FONT_SIZE = 24.sp
private val SIZE_BUTTON_FONT_SIZE = 18.sp
private val DESCRIPTION_FONT_SIZE = 14.sp
private val DESCRIPTION_LINE_HEIGHT = 24.sp
private val HEADER_SUBTITLE_FONT_SIZE = 10.sp
private val LETTER_SPACING = 0.02.em

@Composable
fun DetailScreen() {
    val viewModel = hiltViewModel<DetailViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val internalLoadingExceptionMessage = stringResource(R.string.detail_internalExceptionMessage)

    LaunchedEffect(Unit) {
        viewModel.labelFlow.collect { label ->
            when (label) {
                is DetailLabel.InternalLoadingException -> Toast.makeText(
                    context,
                    internalLoadingExceptionMessage,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    DetailContent(
        state = state,
        onPlusClicked = { viewModel.onEvent(OnPlusClicked) },
        onMinusClicked = { viewModel.onEvent(OnMinusClicked) },
        onSizeClicked = { viewModel.onEvent(OnSizeClicked(it)) },
        onPizzaSelected = { viewModel.onEvent(OnPizzaSelected(it)) },
    )
}

@Composable
fun DetailContent(
    state: DetailUiState,
    onPlusClicked: () -> Unit,
    onMinusClicked: () -> Unit,
    onSizeClicked: (String) -> Unit,
    onPizzaSelected: (pizzaUrl: String) -> Unit,
) {
    if (state.pizzaList.isEmpty()) return

    var isScreenShown by remember { mutableStateOf(false) }
    val animScreenShowing by animateFloatAsState(
        targetValue = if (isScreenShown) 1f else 0f,
        animationSpec = spring(
            dampingRatio = ANIM_SCREEN_RADIUS_SPRING_DUMP_RATIO,
            stiffness = Spring.StiffnessLow,
        ),
        label = "animScreenShowing",
    )
    val animZoomPizza by animateFloatAsState(
        targetValue = if (state.isZoomMode) 1f else 0f,
        animationSpec = spring(
            dampingRatio = ANIM_SIZE_SPRING_DUMP_RATIO,
            stiffness = Spring.StiffnessLow,
        ),
        label = "animZoomPizza",
    )

    val selectedPizza = state.getSelectedPizza()
    val selectedPizzaSize = selectedPizza.getSelectedVariant().size
    val context = LocalContext.current
    val messageBack = stringResource(R.string.detail_messageBack)
    val messageFavorite = stringResource(R.string.detail_messageFavorite)

    LaunchedEffect(Unit) { isScreenShown = true }

    DetailBackground(animScreenShowing)
    ImageCarousel(
        imageUrlList = state.pizzaList.map { it.imageUrl },
        selectedUrlImage = selectedPizza.imageUrl,
        selectedImageDescription = selectedPizza.name,
        centerImageCenterPoint = getCenterImageCenterPoint(),
        onImageClick = onPizzaSelected,
        fullScreenWidthPx = fullScreenWidthPx(),
        animZoomImage = animZoomPizza,
        animScreenAlpha = animScreenShowing,
        selectedImageSize = selectedPizzaSize,
    )
    Header(
        title = state.title,
        onBackClick = { Toast.makeText(context, messageBack, Toast.LENGTH_LONG).show() },
        onFavoriteClick = { Toast.makeText(context, messageFavorite, Toast.LENGTH_LONG).show() },
        animShift = animScreenShowing,
        animZoomPizza = animZoomPizza,
    )
    SizePicker(
        variantList = selectedPizza.variantList,
        onSizeClicked = onSizeClicked,
        animScreenShift = animScreenShowing,
        animZoomPizza = animZoomPizza,
    )
    BottomContent(
        state = state,
        onPlusClicked = onPlusClicked,
        onMinusClicked = onMinusClicked,
        animScreenShift = animScreenShowing,
        animZoomPizza = animZoomPizza,
    )
}

@Composable
private fun BottomContent(
    state: DetailUiState,
    onPlusClicked: () -> Unit,
    onMinusClicked: () -> Unit,
    animScreenShift: Float,
    animZoomPizza: Float,
) {
    val screenSize = fullScreenSizeDpSize()
    val (radiusPx, circleCenterOffsetPx) = getBackgroundRadiusAndCenterPx()
    val screenWidthPx = fullScreenWidthPx()
    val animSumRadiusPx = calculateAnimatedRadiusPx(radiusPx, animScreenShift, animZoomPizza)
    val xCenterPx = screenWidthPx / 2
    val animYTopPx = lowerCircleYPx(xCenterPx, circleCenterOffsetPx.x, circleCenterOffsetPx.y, animSumRadiusPx)
    val yTopPx = lowerCircleYPx(xCenterPx, circleCenterOffsetPx.x, circleCenterOffsetPx.y, radiusPx)
    val animYTopDp = animYTopPx.toDp()
    val yTopDp = yTopPx.toDp()
    val height = screenSize.height - yTopDp

    Column(
        modifier = Modifier
            .offset(0.dp, animYTopDp)
            .fillMaxWidth(1f)
            .height(height)
            .padding(
                top = BOTTOM_CONTENT_PADDING_TOP,
                bottom = BOTTOM_CONTENT_PADDING_BOTTOM,
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = state.getSelectedPizza().description,
            fontSize = DESCRIPTION_FONT_SIZE,
            lineHeight = DESCRIPTION_LINE_HEIGHT,
            color = ActiveColor,
            modifier = Modifier
                .padding(
                    start = DESCRIPTION_PADDING_START,
                    end = DESCRIPTION_PADDING_END,
                ),
        )
        AddPizza(
            pizzaCount = state.pizzaCount,
            price = state.fullPrice,
            onPlusClicked = onPlusClicked,
            onMinusClicked = onMinusClicked,
        )
    }
}

@Composable
private fun AddPizza(
    pizzaCount: Int,
    price: Float,
    onPlusClicked: () -> Unit,
    onMinusClicked: () -> Unit,
) {
    val context = LocalContext.current
    val messageAdded = stringResource(R.string.detail_messageAdded)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = HORIZONTAL_SCREEN_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(HighlightColor, CircleShape)
                .size(COUNTER_WIDTH, COUNTER_HEIGHT),
        ) {
            Image(
                painter = painterResource(R.drawable.svg_minus),
                contentDescription = stringResource(R.string.detail_descriptionDecrease),
                modifier = Modifier
                    .dropShadow(
                        shape = CircleShape,
                        shadow = createShadow(),
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onMinusClicked),
            )
            Text(
                text = pizzaCount.toString(),
                color = ActiveColor,
                fontSize = LARGE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
            )
            Image(
                painter = painterResource(R.drawable.svg_plus),
                contentDescription = stringResource(R.string.detail_descriptionIncrease),
                modifier = Modifier
                    .dropShadow(
                        shape = CircleShape,
                        shadow = createShadow(),
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onPlusClicked),
            )
        }
        Text(
            text = stringResource(R.string.detail_priceFormat, price),
            fontSize = LARGE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            color = ActiveColor,
        )
        Button(
            onClick = {
                Toast.makeText(context, messageAdded, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .sizeIn(ADD_BUTTON_MIN_WIDTH, ADD_BUTTON_MIN_HEIGHT),
        ) {
            Text(
                text = stringResource(R.string.detail_add),
                fontSize = LARGE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = White,
            )
        }
    }
}

@Composable
private fun SizePicker(
    variantList: List<VariantUi>,
    onSizeClicked: (String) -> Unit,
    animScreenShift: Float,
    animZoomPizza: Float,
) {
    val distributionList = distributePoints(variantList.size)
    val screenWidthPx = fullScreenWidthPx()
    val (radiusPx, circleCenterOffsetPx) = getBackgroundRadiusAndCenterPx()
    val animSumRadiusPx = calculateAnimatedRadiusPx(radiusPx, animScreenShift, animZoomPizza)

    Banana(screenWidthPx, animSumRadiusPx, circleCenterOffsetPx)

    variantList.onEachIndexed { index, variant ->
        val xCenterPx = screenWidthPx * distributionList[index]
        val yCenterPx = lowerCircleYPx(xCenterPx, circleCenterOffsetPx.x, circleCenterOffsetPx.y, animSumRadiusPx)
        val xCenterDp = xCenterPx.toDp()
        val yCenterDp = yCenterPx.toDp()
        val isSelected = variant.isSelected
        val buttonSize = if (isSelected) SIZE_BUTTON_SELECTED_SIZE else SIZE_BUTTON_SIZE

        BoxXY(
            width = buttonSize,
            height = buttonSize,
            xCenterDp = xCenterDp,
            yCenterDp = yCenterDp,
            modifier = Modifier
                .run {
                    if (isSelected) {
                        clip(CircleShape)
                            .background(White)
                            .padding(SIZE_BUTTON_SELECTED_BORDER)
                    } else {
                        dropShadow(
                            shape = CircleShape,
                            shadow = createShadow(),
                        )
                    }
                }
                .background(if (isSelected) ActiveColor else White, CircleShape)
                .clip(CircleShape)
                .clickable { onSizeClicked(variant.size) },
        ) {
            Text(
                text = variant.size,
                fontSize = SIZE_BUTTON_FONT_SIZE,
                color = if (isSelected) OnActiveColor else TextAndIconColor,
                letterSpacing = LETTER_SPACING,
            )
        }
    }
}

@Composable
private fun Banana(
    screenWidthPx: Float,
    radiusPx: Float,
    circleCenterOffsetPx: Offset,
) {
    val xCenterPx = screenWidthPx / 2
    val bananaRadiusPx = radiusPx * BANANA_RADIUS_RATIO_TO_BACKGROUND_RADIUS
    val yCenterPx = lowerCircleYPx(xCenterPx, circleCenterOffsetPx.x, circleCenterOffsetPx.y, bananaRadiusPx)
    val xCenterDp = xCenterPx.toDp()
    val yCenterDp = yCenterPx.toDp()

    BoxXY(
        width = BANANA_WIDTH,
        height = BANANA_HEIGHT,
        xCenterDp = xCenterDp,
        yCenterDp = yCenterDp,
    ) {
        Image(
            painter = painterResource(R.drawable.img_banana),
            contentDescription = stringResource(R.string.detail_descriptionBanana),
        )
    }
}

@Composable
private fun Header(
    title: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    animShift: Float,
    animZoomPizza: Float,
) {
    val animScreenOffsetXY = HEADER_BUTTON_SIZE * HEADER_HIDDEN_OFFSET_IN_BUTTON_SIZES * (1f - animShift)
    val animZoomOffsetXY = HEADER_ZOOM_OFFSET * animZoomPizza
    val animSumOffsetXY = animScreenOffsetXY + animZoomOffsetXY

    Column {
        StatusBarSpacer()
        Spacer(
            modifier = Modifier.height(HEADER_TOP_SPACE),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(
                    start = HORIZONTAL_SCREEN_PADDING,
                    top = HEADER_PADDING_TOP,
                    end = HORIZONTAL_SCREEN_PADDING,
                ),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.svg_back),
                contentDescription = stringResource(R.string.detail_descriptionBack),
                modifier = Modifier
                    .offset(x = -animSumOffsetXY)
                    .size(HEADER_BUTTON_SIZE)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = createShadow(),
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.detail_titleSmall),
                    fontSize = HEADER_SUBTITLE_FONT_SIZE,
                    color = TextAndIconColor,
                    modifier = Modifier
                        .offset(y = -animSumOffsetXY),
                )
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(ANIM_TITLE_ANIMATION_DURATION_MILLIS)) togetherWith
                            fadeOut(animationSpec = tween(ANIM_TITLE_ANIMATION_DURATION_MILLIS))
                    },
                    label = "Title animation",
                    modifier = Modifier
                        .offset(y = -animSumOffsetXY),
                ) { targetText ->
                    Text(
                        text = targetText,
                        fontSize = LARGE_FONT_SIZE,
                        lineHeight = LARGE_FONT_SIZE,
                        letterSpacing = LETTER_SPACING,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.svg_favorite),
                contentDescription = stringResource(R.string.detail_descriptionFavorite),
                modifier = Modifier
                    .offset(x = animSumOffsetXY)
                    .size(HEADER_BUTTON_SIZE)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = createShadow(),
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onFavoriteClick),
            )
        }
    }
}

@Composable
private fun DetailBackground(animScreenRadiusScale: Float) {
    val (radiusPx, circleCenterOffsetPx) = getBackgroundRadiusAndCenterPx()
    Canvas(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(White),
    ) {
        drawCircle(
            color = HighlightColor,
            radius = radiusPx * (BACKGROUND_INITIAL_RADIUS_SCALE - animScreenRadiusScale),
            center = circleCenterOffsetPx,
        )
    }
}

@Composable
private fun BoxXY(
    width: Dp,
    height: Dp,
    xCenterDp: Dp,
    yCenterDp: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = { },
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width, height)
            .offset(
                x = xCenterDp - width / 2,
                y = yCenterDp - height / 2,
            )
            .then(modifier),
        content = content,
    )
}

@Composable
private fun getCenterImageCenterPoint(): DpOffset {
    val (_, circleCenterOffsetPx) = getBackgroundRadiusAndCenterPx()
    val radiusPx = CENTER_IMAGE_CIRCLE_RADIUS.toPx()
    val x = fullScreenWidthDp().toPx() / 2
    val y = lowerCircleYPx(x, circleCenterOffsetPx.x, circleCenterOffsetPx.y, radiusPx)

    return DpOffset(x.toDp(), y.toDp())
}

@Composable
private fun getBackgroundRadiusAndCenterPx(): Pair<Float, Offset> {
    val widthPx = fullScreenWidthPx()
    val heightPx = fullScreenHeightPx()

    val radius = heightPx * RADIUS_RATIO_TO_SCREEN_HEIGHT
    val circleCenter = Offset(
        x = widthPx / 2,
        y = heightPx * CENTER_POINT_RATIO_TO_SCREEN_HEIGHT,
    )

    return Pair(radius, circleCenter)
}

private fun calculateAnimatedRadiusPx(radiusPx: Float, animScreenShift: Float, animZoomPizza: Float): Float {
    val animScreenRadiusPx = radiusPx * (BACKGROUND_INITIAL_RADIUS_SCALE - animScreenShift)
    val animZoomRadiusPx = radiusPx * BACKGROUND_ZOOM_RADIUS_SCALE * animZoomPizza

    return animScreenRadiusPx + animZoomRadiusPx
}

private fun lowerCircleYPx(x: Float, centerX: Float, centerY: Float, radius: Float): Float {
    val dx = x - centerX
    val underRoot = radius * radius - dx * dx

    return centerY + sqrt(underRoot)
}

@Preview
@Composable
private fun Preview() {
    DetailContent(
        state = DetailUiState(
            title = "Midnight Harvest",
            selectedPizzaId = "1",
            pizzaList = listOf(
                PizzaUi(
                    id = "1",
                    name = "Midnight Harvest",
                    imageUrl = "https://oursongapp.com/images/pizzas/pizza_midnight_harvest.png",
                    description = "This pizza celebrates the rich and bold flavors of black olives paired with " +
                        "a medley of cheeses. The deep, earthy taste of black olives harmonizes beautifully " +
                        "with the creamy, melted cheeses.",
                    variantList = listOf(
                        VariantUi(
                            size = VariantUi.SIZE_SMALL,
                            isSelected = true,
                        ),
                        VariantUi(
                            size = VariantUi.SIZE_MEDIUM,
                            isSelected = false,
                        ),
                        VariantUi(
                            size = VariantUi.SIZE_LARGE,
                            isSelected = false,
                        ),
                    ),
                ),
            ),
            pizzaCount = 2,
            fullPrice = 33.333f,
        ),
        onPlusClicked = { },
        onMinusClicked = { },
        onSizeClicked = { },
        onPizzaSelected = { },
    )
}
