package io.github.droidkaigi.confsched.core.model

import soil.query.SubscriptionKey

/** Emits `null` until the user creates their card. */
typealias ProfileCardSubscriptionKey = SubscriptionKey<ProfileCard?>
