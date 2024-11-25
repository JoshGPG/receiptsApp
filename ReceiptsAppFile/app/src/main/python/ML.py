import pandas as pd
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt
import io
import json
import os


def load_data(file_content):
    data = pd.read_csv(io.StringIO(file_content))
    data['date_purchased'] = pd.to_datetime(data['date_purchased'])
    return data

def get_recent_data(user_id, data, months=3):
    user_data = data[data['user_id'] == user_id]
    latest_date = user_data['date_purchased'].max()
    start_date = latest_date - pd.DateOffset(months=months)
    recent_data = user_data[(user_data['date_purchased'] >= start_date) & (user_data['date_purchased'] <= latest_date)]
    return recent_data

def calculate_spending_proportion(recent_data):
    if recent_data.empty:
        return pd.DataFrame(), []
    recent_data['date_purchased'] = pd.to_datetime(recent_data['date_purchased'], errors='coerce')
    recent_data['price'] = pd.to_numeric(recent_data['price'], errors='coerce').fillna(0)
    recent_data['month'] = recent_data['date_purchased'].dt.to_period('M')
    monthly_total = recent_data.groupby('month')['price'].sum()
    monthly_spending = recent_data.groupby(['month', 'category'])['price'].sum().unstack(fill_value=0)
    monthly_percentage = monthly_spending.div(monthly_total, axis=0).fillna(0)
    categories = ['clothing', 'entertainment', 'electronics', 'home', 'health and personal', 'groceries']
    for category in categories:
        if category not in monthly_percentage.columns:
            monthly_percentage[category] = 0
    monthly_percentage = monthly_percentage[categories]
    return monthly_percentage, categories

def predict_next_month_proportion(monthly_percentage, categories):
    X_train = monthly_percentage.values
    if X_train.shape[0] < 3:
        X_train = np.pad(X_train, ((3 - X_train.shape[0], 0), (0, 0)), 'constant')
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
        tf.keras.layers.Dense(32, activation='relu'),
        tf.keras.layers.Dense(len(categories), activation='softmax')
    ])
    model.compile(optimizer='adam', loss='mse')
    model.fit(X_train, X_train, epochs=10, verbose=0)
    predicted_next_month_proportion = model.predict(np.array([X_train[-1]]))
    predicted_next_month_proportion = np.clip(predicted_next_month_proportion, 0, None)
    return predicted_next_month_proportion.flatten()

def allocate_budget(predicted_proportion, total_budget, categories):
    predicted_next_month_df = pd.DataFrame({
        'Category': categories,
        'Predicted_Proportion': predicted_proportion
    })
    predicted_next_month_df['Predicted_Budget'] = predicted_next_month_df['Predicted_Proportion'] * total_budget

    # Format output as requested
    result = "Suggested Budget Allocation for Next Month:\n"
    for _, row in predicted_next_month_df.iterrows():
        result += f"{row['Category']}: {row['Predicted_Budget']:.2f}\n"

    return result

def plot_spending_proportion(monthly_percentage, user_id):
    monthly_percentage.plot(kind='bar', stacked=True, figsize=(10, 6))
    plt.title(f"Monthly Spending Proportion for User {user_id}")
    plt.ylabel("Proportion of Total Spending")
    plt.xlabel("Month")
    plt.xticks(rotation=45)
    plt.tight_layout()
    image_path = f"spending_proportion_user_{user_id}.png"
    plt.savefig(image_path)
    plt.close()
    return image_path

def save_budget_to_file(predicted_proportion, total_budget, categories):
    predicted_next_month_df = pd.DataFrame({
        'Category': categories,
        'Predicted_Proportion': predicted_proportion
    })
    predicted_next_month_df['Predicted_Budget'] = predicted_next_month_df['Predicted_Proportion'] * total_budget

    # Ensure the JSON format is compatible
    result = []
    for _, row in predicted_next_month_df.iterrows():
        result.append({
            'Category': row['Category'],
            'Predicted_Proportion': f"{row['Predicted_Proportion']:.4f}",
            'Predicted_Budget': f"{row['Predicted_Budget']:.2f}"
        })

    # Save file to cache directory
    cache_path = os.getenv('HOME', '/receiptsApp/ReceiptsAppFile/app/.idea/caches')
    file_path = os.path.join(cache_path, 'budget_output.json')

    with open(file_path, 'w', encoding='utf-8') as json_file:
        json.dump(result, json_file)

    return file_path








