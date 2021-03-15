//
//  IntegrationViewModel.swift
//  IntegrationTestApp
//
//  Created by Alexander Shmakov on 16.05.2020.
//  Copyright © 2020 IntelligenceRetail. All rights reserved.
//

import Foundation
import Combine
import UIKit

class IntegrationViewModel: ObservableObject {

    // MARK: Input
    @Published var login: String = ""
    @Published var password: String = ""
    @Published var userId: String = ""
    @Published var storeId: String = ""
    @Published var visitId: String = ""
    @Published var taskId: String = ""

    init() {
        print("IntegrationViewModel inited")
    }

    // MARK: Output
    @Published var requestAnswer: String = ""

    // MARK: Actions

    func startVisit() {
        makeRequestWithMethodName("visit")
    }

    func requestReports() {
        makeRequestWithMethodName("report")
    }

    func requestSummaryReports() {
        makeRequestWithMethodName("summaryReport")
    }

    func requestSync() {
        makeRequestWithMethodName("sync")
    }

    // Launch Intelligence Retail App with necessary parameters
    func makeRequestWithMethodName(_ methodName: String) {

        // Create URL via URLComponents with scheme "intelligenceretail" and parameters.

        // Required for all requests:
        //
        // "method" - method name. 4 methods available: visit, report, summaryReport, sync
        // "login"
        // "password"

        // Optional parameters:
        //
        // "user_id" - required for all methods if users use external id.
        // "store_id" - required for visit method request
        // "visit_id" - required for visit, report, summaryReport request
        // "back_url_scheme" - required for report request. Your application custom URL scheme, located in Info.plist. Intelligence Retail app will open url with the scheme and "report" parameter. Handle it in AppDelegate(<iOS 13.0) or SceneDelegate(>=iOS 13.0).


        var components = URLComponents()
        components.scheme = "intelligenceretail"
        components.queryItems = [URLQueryItem(name: "method", value: methodName),
                                 URLQueryItem(name: "login", value: login),
                                 URLQueryItem(name: "password", value: password),
                                 URLQueryItem(name: "user_id", value: userId),
                                 URLQueryItem(name: "store_id", value: storeId),
                                 URLQueryItem(name: "visit_id", value: visitId),
                                 URLQueryItem(name: "back_url_scheme", value: "integrationtestapp")]
        if (!taskId.isEmpty){
            components.queryItems?.append(URLQueryItem(name: "task_id", value: taskId))
        }
        let url = components.url!

        // Open URL
        UIApplication.shared.open(url, options: [:]) { (completed) in
            print(completed)
        }
    }
}
