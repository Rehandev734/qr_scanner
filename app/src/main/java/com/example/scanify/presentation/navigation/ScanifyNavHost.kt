package com.example.scanify.presentation.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.scanify.presentation.favorites.FavoritesScreen
import com.example.scanify.presentation.generated.GeneratedDetailScreen
import com.example.scanify.presentation.generated.GeneratedHistoryScreen
import com.example.scanify.presentation.generator.GeneratorScreen
import com.example.scanify.presentation.history.HistoryScreen
import com.example.scanify.presentation.home.HomeScreen
import com.example.scanify.presentation.result.ResultScreen
import com.example.scanify.presentation.scanner.ScannerScreen
import com.example.scanify.presentation.settings.AboutScreen
import com.example.scanify.presentation.settings.SettingsScreen
import com.example.scanify.presentation.splash.SplashScreen
import com.example.scanify.presentation.statistics.StatisticsScreen

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SCANNER = "scanner"
    const val RESULT = "result?scan_id={scan_id}&scan_value={scan_value}&scan_format={scan_format}&scan_type={scan_type}&scan_timestamp={scan_timestamp}"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val GENERATOR = "generator?qr_content={qr_content}&qr_type={qr_type}"
    const val GENERATED_HISTORY = "generated_history"
    const val GENERATED_DETAIL = "generated_detail?qr_id={qr_id}&qr_content={qr_content}&qr_type={qr_type}&qr_timestamp={qr_timestamp}&qr_is_favorite={qr_is_favorite}"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val STATISTICS = "statistics"

    fun resultRoute(
        scanId: Long = 0L,
        value: String = "",
        format: String = "",
        type: String = "",
        timestamp: Long = System.currentTimeMillis()
    ): String {
        return "result?scan_id=$scanId&scan_value=${Uri.encode(value)}&scan_format=${Uri.encode(format)}&scan_type=${Uri.encode(type)}&scan_timestamp=$timestamp"
    }

    fun generatorRoute(
        content: String = "",
        type: String = ""
    ): String {
        return "generator?qr_content=${Uri.encode(content)}&qr_type=${Uri.encode(type)}"
    }

    fun generatedDetailRoute(
        qrId: Long = 0L,
        content: String = "",
        type: String = "",
        timestamp: Long = System.currentTimeMillis(),
        isFavorite: Boolean = false
    ): String {
        return "generated_detail?qr_id=$qrId&qr_content=${Uri.encode(content)}&qr_type=${Uri.encode(type)}&qr_timestamp=$timestamp&qr_is_favorite=$isFavorite"
    }
}

@Composable
fun ScanifyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(320)
            ) + fadeIn(animationSpec = tween(320))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(320)
            ) + fadeOut(animationSpec = tween(320))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(320)
            ) + fadeIn(animationSpec = tween(320))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(320)
            ) + fadeOut(animationSpec = tween(320))
        }
    ) {
        composable(
            route = Routes.SPLASH,
            enterTransition = { fadeIn(tween(350)) },
            exitTransition = { fadeOut(tween(350)) }
        ) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                onNavigateToGenerator = { navController.navigate(Routes.generatorRoute()) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                onNavigateToStatistics = { navController.navigate(Routes.STATISTICS) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToResult = { scan ->
                    navController.navigate(
                        Routes.resultRoute(
                            scanId = scan.id,
                            value = scan.rawValue,
                            format = scan.format,
                            type = scan.scanType,
                            timestamp = scan.timestamp
                        )
                    )
                },
                onNavigateToGeneratedDetail = { qr ->
                    navController.navigate(
                        Routes.generatedDetailRoute(
                            qrId = qr.id,
                            content = qr.content,
                            type = qr.type,
                            timestamp = qr.timestamp,
                            isFavorite = qr.isFavorite
                        )
                    )
                }
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { scan ->
                    navController.navigate(
                        Routes.resultRoute(
                            scanId = scan.id,
                            value = scan.rawValue,
                            format = scan.format,
                            type = scan.scanType,
                            timestamp = scan.timestamp
                        )
                    ) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToGenerator = { navController.navigate(Routes.generatorRoute()) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument("scan_id") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("scan_value") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("scan_format") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("scan_type") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("scan_timestamp") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scan_id") ?: 0L
            val scanValue = backStackEntry.arguments?.getString("scan_value") ?: ""
            val scanFormat = backStackEntry.arguments?.getString("scan_format") ?: ""
            val scanType = backStackEntry.arguments?.getString("scan_type") ?: ""
            val scanTimestamp = backStackEntry.arguments?.getLong("scan_timestamp") ?: System.currentTimeMillis()

            ResultScreen(
                scanId = scanId,
                scanValue = scanValue,
                scanFormat = scanFormat,
                scanType = scanType,
                scanTimestamp = scanTimestamp,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { scan ->
                    navController.navigate(
                        Routes.resultRoute(
                            scanId = scan.id,
                            value = scan.rawValue,
                            format = scan.format,
                            type = scan.scanType,
                            timestamp = scan.timestamp
                        )
                    )
                },
                onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                onNavigateToGeneratedDetail = { qr ->
                    navController.navigate(
                        Routes.generatedDetailRoute(
                            qrId = qr.id,
                            content = qr.content,
                            type = qr.type,
                            timestamp = qr.timestamp,
                            isFavorite = qr.isFavorite
                        )
                    )
                }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { scan ->
                    navController.navigate(
                        Routes.resultRoute(
                            scanId = scan.id,
                            value = scan.rawValue,
                            format = scan.format,
                            type = scan.scanType,
                            timestamp = scan.timestamp
                        )
                    )
                },
                onNavigateToGeneratedDetail = { qr ->
                    navController.navigate(
                        Routes.generatedDetailRoute(
                            qrId = qr.id,
                            content = qr.content,
                            type = qr.type,
                            timestamp = qr.timestamp,
                            isFavorite = qr.isFavorite
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.GENERATOR,
            arguments = listOf(
                navArgument("qr_content") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("qr_type") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val qrContent = backStackEntry.arguments?.getString("qr_content") ?: ""
            val qrType = backStackEntry.arguments?.getString("qr_type") ?: ""

            GeneratorScreen(
                initialContent = qrContent,
                initialType = qrType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate(Routes.GENERATED_HISTORY) }
            )
        }

        composable(Routes.GENERATED_HISTORY) {
            GeneratedHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id, content, type, timestamp, isFavorite ->
                    navController.navigate(
                        Routes.generatedDetailRoute(
                            qrId = id,
                            content = content,
                            type = type,
                            timestamp = timestamp,
                            isFavorite = isFavorite
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.GENERATED_DETAIL,
            arguments = listOf(
                navArgument("qr_id") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("qr_content") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("qr_type") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("qr_timestamp") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("qr_is_favorite") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val qrId = backStackEntry.arguments?.getLong("qr_id") ?: 0L
            val qrContent = backStackEntry.arguments?.getString("qr_content") ?: ""
            val qrType = backStackEntry.arguments?.getString("qr_type") ?: ""
            val qrTimestamp = backStackEntry.arguments?.getLong("qr_timestamp") ?: System.currentTimeMillis()
            val qrIsFavorite = backStackEntry.arguments?.getBoolean("qr_is_favorite") ?: false

            GeneratedDetailScreen(
                qrId = qrId,
                qrContent = qrContent,
                qrType = qrType,
                qrTimestamp = qrTimestamp,
                qrIsFavorite = qrIsFavorite,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { _, content, type, _ ->
                    navController.navigate(
                        Routes.generatorRoute(
                            content = content,
                            type = type
                        )
                    )
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
