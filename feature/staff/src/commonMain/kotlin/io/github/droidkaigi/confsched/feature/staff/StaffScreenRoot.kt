package io.github.droidkaigi.confsched.feature.staff

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery

@Composable
context(screenContext: StaffScreenContext)
fun StaffScreenRoot(
    onNavigateBack: () -> Unit,
    onNavigateToStaffProfile: (String) -> Unit,
) {
    SoilDataBoundary(state = rememberQuery(screenContext.staffQueryKey)) { staff ->
        val uiState = context(screenContext.presenterContext) {
            staffScreenPresenter(staff)
        }
        StaffScreen(
            uiState = uiState,
            onBackClick = onNavigateBack,
            onStaffClick = onNavigateToStaffProfile,
        )
    }
}
