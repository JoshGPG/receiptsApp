import pandas as pd
import numpy as np
import tensorflow as tf
def load_keras_model(model_path):
    """
    Load a .keras model from the specified path.
    """
    try:
        model = tf.keras.models.load_model(model_path)
        print(f"Model loaded successfully from: {model_path}")
        return model
    except Exception as e:
        print(f"Error loading model: {e}")
        return None
def preprocess_data(data):
        """
        Preprocess the data to ensure it matches the model input requirements.
        """
    # Ensure 'date_purchased' is datetime
    data['date_purchased'] = pd.to_datetime(data['date_purchased'], errors='coerce')
    data['price'] = pd.to_numeric(data['price'], errors='coerce').fillna(0)

    # Group by month and category
    data['month'] = data['date_purchased'].dt.to_period('M')
    monthly_total = data.groupby('month')['price'].sum()
    monthly_spending = data.groupby(['month', 'category'])['price'].sum().unstack(fill_value=0)
    monthly_percentage = monthly_spending.div(monthly_total, axis=0).fillna(0)

    # Ensure fixed categories (e.g., 10 input features)
    categories = ['clothing', 'entertainment', 'electronics', 'home', 'health and personal', 'groceries']
    while len(categories) < 10:
        categories.append(f'category_{len(categories) + 1}')

    monthly_percentage = monthly_percentage.reindex(columns=categories, fill_value=0)
    return monthly_percentage, categories
def predict_budget_allocation(model, data, total_budget):

    # Preprocess data
    monthly_percentage, categories = preprocess_data(data)

    # Prepare input for the model
    X_input = monthly_percentage.values
    if X_input.shape[0] < 3:  # Ensure at least 3 rows for prediction
        X_input = np.pad(X_input, ((3 - X_input.shape[0], 0), (0, 0)), 'constant')
    input_data = np.array([X_input[-1]])  # Use the last month's data

    # Make prediction
    predicted_proportions = model.predict(input_data).flatten()
    predicted_proportions = np.clip(predicted_proportions, 0, None)  # Ensure no negative values

    # Ensure categories and predicted proportions have the same length
    if len(predicted_proportions) != len(categories):
        min_length = min(len(predicted_proportions), len(categories))
        predicted_proportions = predicted_proportions[:min_length]
        categories = categories[:min_length]

    # Allocate budget
    allocation = pd.DataFrame({
        'Category': categories,
        'Predicted_Proportion': predicted_proportions
    })
    allocation['Predicted_Budget'] = allocation['Predicted_Proportion'] * total_budget

    # Format results
    result = "Suggested Budget Allocation for Next Month:\n"
    for _, row in allocation.iterrows():
        result += f"{row['Category']}: ${row['Predicted_Budget']:.2f}\n"

    return result
# Example usage
def main_workflow(model_path):
    """
    Main workflow for loading the model, simulating data, and predicting budget allocation.

    Args:
        model_path (str): Path to the saved .keras model.

    Returns:
        result: Predicted budget allocation results.
    """
    # Load the .keras model
    model = load_keras_model(model_path)

    if model is None:
        print("Model could not be loaded. Exiting workflow.")
        return None

    # Simulate input data
    data = pd.DataFrame({
        "user_id": [1, 1, 1, 1],
        "date_purchased": ["2024-09-01", "2024-10-01", "2024-11-01", "2024-12-01"],
        "category": ["clothing", "groceries", "electronics", "health and personal"],
        "price": [200, 150, 300, 100]
    })

    # Get budget input from user
    total_budget = get_user_budget()

    # Predict budget allocation
    result = predict_budget_allocation(model, data, total_budget)
    print("Predicted Budget Allocation:")
    print(result)
    return result

def get_user_budget():
    """
    Prompt the user to enter their total budget for the next month.

    Returns:
        total_budget (float): The user's total budget.
    """
    while True:
        try:
            total_budget = float(input("Enter your total budget for next month: "))
            return total_budget
        except ValueError:
            print("Invalid input. Please enter a numeric value.")


    # Save file to cache directory
    cache_path = os.getenv('HOME', '/receiptsApp/ReceiptsAppFile/app/.idea/caches')
    file_path = os.path.join(cache_path, 'budget_output.json')

    with open(file_path, 'w', encoding='utf-8') as json_file:
        json.dump(result, json_file)

    return file_path