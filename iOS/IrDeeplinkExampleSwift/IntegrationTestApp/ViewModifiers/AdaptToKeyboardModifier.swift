//
//  AdaptingToKeyboardModifier.swift
//  IntegrationTestApp
//
//  Created by Alexander Shmakov on 18.05.2020.
//  Copyright © 2020 IntelligenceRetail. All rights reserved.
//

import SwiftUI
import Combine

struct AdaptToKeyboardModifier: ViewModifier {
  @State var currentHeight: CGFloat = 0

  func body(content: Content) -> some View {
    content
      .padding(.bottom, currentHeight)
      .edgesIgnoringSafeArea(.bottom)
      .onAppear(perform: subscribeToKeyboardEvents)
  }

  private func subscribeToKeyboardEvents() {
    NotificationCenter.Publisher(
      center: NotificationCenter.default,
      name: UIResponder.keyboardWillShowNotification
    ).compactMap { notification in
        notification.userInfo?["UIKeyboardFrameEndUserInfoKey"] as? CGRect
    }.map { rect in
      rect.height
    }.subscribe(Subscribers.Assign(object: self, keyPath: \.currentHeight))

    NotificationCenter.Publisher(
      center: NotificationCenter.default,
      name: UIResponder.keyboardWillHideNotification
    ).compactMap { notification in
      CGFloat.zero
    }.subscribe(Subscribers.Assign(object: self, keyPath: \.currentHeight))
  }
}
