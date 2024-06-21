//
//  MediapipeLLMInferenceDelegate.swift
//  iosApp
//
//  Created by 2BAB on 20/6/24.
//

import Foundation
import MediaPipeTasksGenAI
import composeApp


class LLMOperatorSwiftImpl: LLMOperator {
    let errorTag = "Mediapipe-LLM-Tasks-iOS: "
    let llmInference: LlmInference
    
    
    init() throws {
        let path = Bundle.main.path(forResource: "gemma-2b-it-gpu-int4", ofType: "bin")!
        let llmOptions =  LlmInference.Options(modelPath: path)
        
        llmInference = try LlmInference(options: llmOptions)
    }
    
    func generateResponse(inputText: String) async throws -> String {
        return try llmInference.generateResponse(inputText: inputText)
    }
    
    func generateResponseAsync(inputText: String, progress: @escaping (String) -> Void, completion: @escaping (String) -> Void) async throws {
        var partialResult = ""
        try llmInference.generateResponseAsync(inputText: inputText) { partialResponse, error in
            // progress
            if let e = error {
                print("\(self.errorTag) \(e)")
                completion(e.localizedDescription)
                return
            }
            if let partial = partialResponse {
                partialResult += partial
                progress(partialResult.trimmingCharacters(in: .whitespacesAndNewlines))
            }
        } completion: {
            completion(partialResult.trimmingCharacters(in: .whitespacesAndNewlines))
            partialResult = ""
        }
    }
    
    func sizeInTokens(text: String) -> Int32 {
        return 0
    }
    
}
