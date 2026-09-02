package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleStrokeControlsRow
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.RecordedOffScreen
import io.github.droidkaigi.confsched.core.ui.isExpandedWindowWidth
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardBack
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFaceDefaults
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFront
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import kotlinx.coroutines.launch

@Composable
fun ProfileCardView(
    uiState: ProfileCardScreenUiState.Card,
    colorScheme: KaigiColorScheme,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: (ImageBitmap) -> Unit,
    onStartDoodlingClick: () -> Unit,
    onDoodlesDoneClick: (Doodle, Doodle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shareImageLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var penSize by rememberSerializable { mutableStateOf(DoodlePenSize.Normal) }
    var selectedInk by rememberSerializable { mutableStateOf(DoodleInk.Ink) }
    var outlined by rememberSerializable { mutableStateOf(true) }
    // A doodle session starts from what is saved and reaches the data layer only on Done, so the
    // drafts are keyed on the session rather than kept for the life of the screen.
    var frontDraft by rememberSerializable(uiState.isDoodling) { mutableStateOf(uiState.frontDoodle) }
    var backDraft by rememberSerializable(uiState.isDoodling) { mutableStateOf(uiState.backDoodle) }
    // A gesture belongs to the pointer holding it, not to the doodle session, so it is never restored.
    var frontGestureActive by remember { mutableStateOf(false) }
    var backGestureActive by remember { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val sideBySide = uiState.isDoodling && maxWidth.isExpandedWindowWidth
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ProfileCardViewDefaults.cardSpacePadding)
                .padding(bottom = LocalNavigationBarOccupiedHeight.current),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ProfileCardViewDefaults.cardSpacing, Alignment.CenterVertically),
        ) {
            if (!uiState.isDoodling) {
                FlippableProfileCard(
                    nickName = uiState.nickName,
                    occupation = uiState.occupation,
                    link = uiState.link,
                    mascot = uiState.mascot,
                    sketchiness = uiState.sketchiness,
                    avatarImage = uiState.avatarImage,
                    frontDoodle = uiState.frontDoodle,
                    backDoodle = uiState.backDoodle,
                    isShowingBack = uiState.isShowingBack,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(onClick = onCardClick),
                )
            } else if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = ProfileCardViewDefaults.cardSpacing,
                        alignment = Alignment.CenterHorizontally,
                    ),
                ) {
                    ProfileCardDoodleFaceColumn(
                        showsBack = false,
                        doodle = frontDraft,
                        nickName = uiState.nickName,
                        occupation = uiState.occupation,
                        link = uiState.link,
                        mascot = uiState.mascot,
                        sketchiness = uiState.sketchiness,
                        avatarImage = uiState.avatarImage,
                        penSize = penSize,
                        selectedInk = selectedInk,
                        outlined = outlined,
                        onStrokeAdd = { frontDraft = frontDraft.withStroke(it) },
                        onGestureActiveChange = { frontGestureActive = it },
                        onUndoClick = { frontDraft = frontDraft.withoutLastStroke() },
                        onClearClick = { frontDraft = Doodle.Empty },
                        modifier = Modifier.fillMaxHeight().weight(1f),
                    )
                    ProfileCardDoodleFaceColumn(
                        showsBack = true,
                        doodle = backDraft,
                        nickName = uiState.nickName,
                        occupation = uiState.occupation,
                        link = uiState.link,
                        mascot = uiState.mascot,
                        sketchiness = uiState.sketchiness,
                        avatarImage = uiState.avatarImage,
                        penSize = penSize,
                        selectedInk = selectedInk,
                        outlined = outlined,
                        onStrokeAdd = { backDraft = backDraft.withStroke(it) },
                        onGestureActiveChange = { backGestureActive = it },
                        onUndoClick = { backDraft = backDraft.withoutLastStroke() },
                        onClearClick = { backDraft = Doodle.Empty },
                        modifier = Modifier.fillMaxHeight().weight(1f),
                    )
                }
            } else {
                ProfileCardDoodleCanvasView(
                    nickName = uiState.nickName,
                    occupation = uiState.occupation,
                    link = uiState.link,
                    mascot = uiState.mascot,
                    sketchiness = uiState.sketchiness,
                    avatarImage = uiState.avatarImage,
                    showsBack = uiState.isShowingBack,
                    doodle = if (uiState.isShowingBack) backDraft else frontDraft,
                    penSize = penSize,
                    selectedInk = selectedInk,
                    outlined = outlined,
                    onStrokeAdd = { stroke ->
                        if (uiState.isShowingBack) {
                            backDraft = backDraft.withStroke(stroke)
                        } else {
                            frontDraft = frontDraft.withStroke(stroke)
                        }
                    },
                    onGestureActiveChange = { active ->
                        if (uiState.isShowingBack) {
                            backGestureActive = active
                        } else {
                            frontGestureActive = active
                        }
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                )
            }
            AnimatedContent(
                targetState = uiState.isDoodling,
                transitionSpec = {
                    val fade = tween<Float>(ProfileCardViewDefaults.controlsDurationMillis)
                    val slide = tween<IntOffset>(ProfileCardViewDefaults.controlsDurationMillis)
                    (fadeIn(fade) + slideInVertically(slide) { height -> height / ProfileCardViewDefaults.controlsSlideFraction })
                        .togetherWith(
                            fadeOut(fade) + slideOutVertically(slide) { height -> -height / ProfileCardViewDefaults.controlsSlideFraction },
                        )
                },
            ) { doodling ->
                if (doodling) {
                    ProfileCardDoodleControlsSection(
                        penSize = penSize,
                        selectedInk = selectedInk,
                        outlined = outlined,
                        isShowingBack = uiState.isShowingBack,
                        sideBySide = sideBySide,
                        gestureActive = frontGestureActive || backGestureActive,
                        canEditFront = frontDraft.strokes.isNotEmpty(),
                        canEditBack = backDraft.strokes.isNotEmpty(),
                        onPenSizeClick = { penSize = it },
                        onInkClick = { selectedInk = it },
                        onOutlinedChange = { outlined = it },
                        onFlipClick = onCardClick,
                        onFrontUndoClick = { frontDraft = frontDraft.withoutLastStroke() },
                        onFrontClearClick = { frontDraft = Doodle.Empty },
                        onBackUndoClick = { backDraft = backDraft.withoutLastStroke() },
                        onBackClearClick = { backDraft = Doodle.Empty },
                        onDoneClick = { onDoodlesDoneClick(frontDraft, backDraft) },
                    )
                } else {
                    ProfileCardActionsSection(
                        isSharing = uiState.isSharing,
                        onShareClick = { coroutineScope.launch { onShareClick(shareImageLayer.toImageBitmap()) } },
                        onDoodleClick = onStartDoodlingClick,
                        onEditClick = onEditClick,
                    )
                }
            }
        }
        // Recorded regardless of which face is turned up, so the share image always carries both.
        RecordedOffScreen(layer = shareImageLayer) { recordingModifier ->
            ProfileCardShareImage(
                nickName = uiState.nickName,
                occupation = uiState.occupation,
                link = uiState.link,
                mascot = uiState.mascot,
                sketchiness = uiState.sketchiness,
                avatarImage = uiState.avatarImage,
                frontDoodle = uiState.frontDoodle,
                backDoodle = uiState.backDoodle,
                colorScheme = colorScheme,
                modifier = recordingModifier,
            )
        }
    }
}

/**
 * The card turned over about its vertical axis. The back face is laid out already mirrored so
 * that, once turned past the edge, it reads the right way round and its outline lands exactly on
 * the front's.
 */
@Composable
private fun FlippableProfileCard(
    nickName: String,
    occupation: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    frontDoodle: Doodle,
    backDoodle: Doodle,
    isShowingBack: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isShowingBack) 180f else 0f,
        animationSpec = tween(durationMillis = ProfileCardViewDefaults.flipDurationMillis, easing = FastOutSlowInEasing),
    )
    val showsBack = rotation > 90f
    val lean = rememberProfileCardLean()
    Box(
        modifier = modifier.graphicsLayer {
            // A graphics layer turns about Y before X, so the lean about the screen's horizontal
            // axis lands outside the flip and the back face leans the same way the front does.
            rotationX = lean.value.pitchDegrees
            rotationY = rotation + lean.value.rollDegrees
            cameraDistance = ProfileCardViewDefaults.flipCameraDistance * density
            // The shadow keeps the card reading as lifted off the page while it sways; the blur
            // absorbs the difference between this rounded rectangle and the sketched outline.
            shadowElevation = ProfileCardViewDefaults.cardShadowElevation.toPx()
            shape = RoundedCornerShape(ProfileCardFaceDefaults.cornerRadius)
            clip = false
        },
    ) {
        if (showsBack) {
            ProfileCardBack(
                nickName = nickName,
                link = link,
                mascot = mascot,
                sketchiness = sketchiness,
                doodle = backDoodle,
                taped = false,
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            )
        } else {
            ProfileCardFront(
                nickName = nickName,
                occupation = occupation,
                mascot = mascot,
                sketchiness = sketchiness,
                taped = false,
                avatarImage = avatarImage,
                doodle = frontDoodle,
            )
        }
    }
}

private fun Doodle.withStroke(stroke: DoodleStroke): Doodle = Doodle(strokes = strokes + stroke)

private fun Doodle.withoutLastStroke(): Doodle = Doodle(strokes = strokes.dropLast(1))

@Composable
private fun ProfileCardDoodleFaceColumn(
    showsBack: Boolean,
    doodle: Doodle,
    nickName: String,
    occupation: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    penSize: DoodlePenSize,
    selectedInk: DoodleInk,
    outlined: Boolean,
    onStrokeAdd: (DoodleStroke) -> Unit,
    onGestureActiveChange: (Boolean) -> Unit,
    onUndoClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ProfileCardViewDefaults.faceControlsSpacing),
    ) {
        ProfileCardDoodleCanvasView(
            nickName = nickName,
            occupation = occupation,
            link = link,
            mascot = mascot,
            sketchiness = sketchiness,
            avatarImage = avatarImage,
            showsBack = showsBack,
            doodle = doodle,
            penSize = penSize,
            selectedInk = selectedInk,
            outlined = outlined,
            onStrokeAdd = onStrokeAdd,
            onGestureActiveChange = onGestureActiveChange,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        DoodleStrokeControlsRow(
            canEdit = doodle.strokes.isNotEmpty(),
            onUndoClick = onUndoClick,
            onClearClick = onClearClick,
            modifier = Modifier.widthIn(max = ProfileCardViewDefaults.faceControlsMaxWidth).fillMaxWidth(),
        )
    }
}

private object ProfileCardViewDefaults {
    val flipDurationMillis = 500
    val cardShadowElevation = 8.dp
    val flipCameraDistance = 12f
    val cardSpacePadding = 24.dp
    val cardSpacing = 24.dp
    val faceControlsSpacing = 12.dp
    val faceControlsMaxWidth = 360.dp
    val controlsDurationMillis = 220

    /** The share of the block's own height the controls slide in over. */
    val controlsSlideFraction = 6
}

@LocalePreviews
@Composable
private fun ProfileCardViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardView(
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                mascot = Mascot.C,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
                frontDoodle = Doodle.fakeOnCardFace(),
                backDoodle = Doodle.Empty,
            ),
            colorScheme = colorScheme,
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardViewDoodlingPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardView(
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com/user",
                mascot = Mascot.C,
                sketchiness = Sketchiness.Normal,
                avatarImage = null,
                frontDoodle = Doodle.fakeOnCardFace(),
                backDoodle = Doodle.Empty,
                isDoodling = true,
            ),
            colorScheme = colorScheme,
            onCardClick = {},
            onEditClick = {},
            onShareClick = {},
            onStartDoodlingClick = {},
            onDoodlesDoneClick = { _, _ -> },
        )
    }
}
