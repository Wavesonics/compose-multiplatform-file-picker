//
//  ContentView.swift
//  iosApp
//
//  Created by Edward James Remo on 9/20/23.
//

import SwiftUI
import ios

struct ContentView: View {
    var body: some View {
        ZStack {
            ComposeView()
        }.listRowInsets(EdgeInsets())
    }
}


struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

#Preview {
    ContentView()
}
