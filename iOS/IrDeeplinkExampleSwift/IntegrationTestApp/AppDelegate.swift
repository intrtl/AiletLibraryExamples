//
//  AppDelegate.swift
//  IntegrationTestApp
//
//  Created by Alexander Shmakov on 15.05.2020.
//  Copyright © 2020 IntelligenceRetail. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        return true
    }
    // Receiving report from Intelligence Retail App in ios with version below 13.
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
         // Handle incoming URL and extract param with "result" name from it. Status value is a string.
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
            let resultQueryItem = components.queryItems?.filter({ $0.name == "result" }).first,
            let result = resultQueryItem.value
        else { return false }

        print(result)

        return true
    }

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {

        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
    }
}

