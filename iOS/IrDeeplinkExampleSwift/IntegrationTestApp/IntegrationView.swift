//
//  ContentView.swift
//  IntegrationTestApp
//
//  Created by Alexander Shmakov on 15.05.2020.
//  Copyright © 2020 IntelligenceRetail. All rights reserved.
//

import SwiftUI

struct IntegrationView: View {

    @ObservedObject var viewModel: IntegrationViewModel

    private let textFieldsOffset: CGFloat = 20

    var body: some View {
        NavigationView {
            ScrollView(.vertical) {
                VStack(alignment: .center, spacing: 10) {
                    Group{
                        TextField("Login", text: $viewModel.login)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                        SecureField("Password", text: $viewModel.password)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                        TextField("External User Id", text: $viewModel.userId)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                        TextField("External Store Id", text: $viewModel.storeId)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                        TextField("External Visit Id", text: $viewModel.visitId)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                        TextField("External Task Id", text: $viewModel.taskId)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(.horizontal, textFieldsOffset)
                    }

                    Button(action: viewModel.startVisit,
                           label: { Text(LocalizedStringKey(stringLiteral: "START_VISIT_BUTTON")) })
                    Button(action: viewModel.requestReports,
                           label: { Text(LocalizedStringKey(stringLiteral: "REPORT_BUTTON")) })
                    Button(action: viewModel.requestSummaryReports,
                           label: { Text(LocalizedStringKey(stringLiteral: "SUMMARY_REPORT_BUTTON")) })
                    Button(action: viewModel.requestSync,
                           label: { Text(LocalizedStringKey(stringLiteral: "SYNC_BUTTON")) })

                    
                    Text(LocalizedStringKey(stringLiteral: "RESULT")).font(.headline).frame(width: UIScreen.main.bounds.width, height: nil, alignment: .center)

                }.offset(x: 0, y: 0)
                if #available(iOS 15.0, *) {
                    Text(viewModel.requestAnswer).frame(minWidth: UIScreen.main.bounds.width, minHeight: 20, alignment: .topLeading)
                        .textSelection(.enabled)
                } else {
                    TextField("", text: .constant(viewModel.requestAnswer)).frame(minWidth: UIScreen.main.bounds.width, minHeight: 20, alignment: .topLeading)
                    Text(viewModel.requestAnswer).frame(minWidth: UIScreen.main.bounds.width, minHeight: 20, alignment: .topLeading)
                }
            }
            .navigationBarTitle("Deeplinks Test")
            .onTapGesture {
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
            }
            .modifier(AdaptToKeyboardModifier())
        }
}

}
#if DEBUG
struct IntegrationView_Previews: PreviewProvider {
    static var previews: some View {
        IntegrationView(viewModel: IntegrationViewModel()).environment(\.locale, .init(identifier: "en"))
    }
}
#endif
