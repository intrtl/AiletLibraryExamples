//
//  SceneDelegate.swift
//  IntegrationTestApp
//
//  Created by Alexander Shmakov on 15.05.2020.
//  Copyright © 2020 IntelligenceRetail. All rights reserved.
//

import UIKit
import SwiftUI

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?
    var viewModel = IntegrationViewModel()

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        let contentView = IntegrationView(viewModel: viewModel)
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = UIHostingController(rootView: contentView)
            self.window = window
            window.makeKeyAndVisible()
        }
    }

    // Receiving report from Intelligence Retail App in ios 13 and above.
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {

        // Handle incoming URL and extract param with "result" name from it. Result value is a JSON string.
        guard let url = URLContexts.first?.url,
            let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
            let resultQueryItem = components.queryItems?.filter({ $0.name == "result" }).first,
            let result = resultQueryItem.value
        else { return }
        viewModel.requestAnswer = "\(result)"
    }

    func sceneDidDisconnect(_ scene: UIScene) {
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
    }

    func sceneWillResignActive(_ scene: UIScene) {
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
    }

}
