import ollama

user_input = input("input: ")

print("input:", user_input)

desireModel = 'llama3.1:latest'
questionToAsk='Which of the six categories of Food, Clothing, Electronic, Health and cosmetics, Home, Entertainment does  '+ user_input +' belong to? Health and cosmetics are the same category, with the class name being Health and cosmetics. The answer only needs to be the class name.'

response = ollama.chat(model=desireModel,messages=[
    {
        'role':'user',
        'content':questionToAsk,
    },
]
)

OllamaResponse = response['message']['content']

print(OllamaResponse)

with open("../../../../../../../../../../codes/testLlama/OutputOllama.txt", "w", encoding="utf-8") as text_file:
    text_file.write(OllamaResponse)