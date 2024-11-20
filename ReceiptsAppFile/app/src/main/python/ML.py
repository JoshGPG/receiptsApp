#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt


# In[2]:


file_path = r'C:\Users\Rache\OneDrive - UW-Eau Claire\Desktop\485\Generated_Purchases_for_Users.csv'
data = pd.read_csv(file_path)


# In[3]:


#  Make sure the date field is of type datetime
data['date_purchased'] = pd.to_datetime(data['date_purchased'])


# In[4]:


# Input the user ID and extract the user's data for the most recent three months.
def get_recent_data(user_id, data, months=3):
    # Filter the specified user ID.
    user_data = data[data['user_id'] == user_id]
    # Get the most recent date.
    latest_date = user_data['date_purchased'].max()
    # Calculate the start date for the most recent three months.
    start_date = latest_date - pd.DateOffset(months=months)
    # Filter the data for the most recent three months.
    recent_data = user_data[(user_data['date_purchased'] >= start_date) & (user_data['date_purchased'] <= latest_date)]
    return recent_data


# In[5]:


# Input the user ID.
user_id = int(input("Enter your user ID: "))
recent_data = get_recent_data(user_id, data)


# In[6]:


# Calculate the spending proportion for each category in each month.
recent_data['month'] = recent_data['date_purchased'].dt.to_period('M')
monthly_total = recent_data.groupby('month')['price'].sum()
monthly_spending = recent_data.groupby(['month', 'category'])['price'].sum().unstack().fillna(0)


# In[7]:


# Calculate the spending percentage for each category.
monthly_percentage = monthly_spending.div(monthly_total, axis=0).fillna(0)
categories = ['clothing', 'entertainment', 'electronics', 'home', 'health and personal', 'groceries']
for category in categories:
    if category not in monthly_percentage.columns:
        monthly_percentage[category] = 0


# In[8]:


# Ensure consistency in column order.
monthly_percentage = monthly_percentage[categories]


# In[9]:


# Visualization: Spending proportion trends over the most recent three months.
monthly_percentage.plot(kind='bar', stacked=True, figsize=(10, 6))
plt.title(f"Monthly Spending Proportion per Category for User {user_id}")
plt.ylabel("Proportion of Total Spending")
plt.xlabel("Month")
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()


# In[10]:


# Prepare features for the machine learning model  
# The features are the spending proportions from the most recent three months
X_train = monthly_percentage.values  # Spending proportions for the most recent three months.
if X_train.shape[0] < 3:
    X_train = np.pad(X_train, ((3 - X_train.shape[0], 0), (0, 0)), 'constant')


# In[11]:


print("X_train:")
print(X_train)


# In[12]:


# Build the model using TensorFlow.
model = tf.keras.Sequential([
    tf.keras.layers.Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dense(len(categories), activation='softmax')  # 使用softmax输出比例
])


# In[13]:


# Compile the model.
model.compile(optimizer='adam', loss='mse')


# In[14]:


# Use the model to directly predict the spending proportions for the fourth month.
model.fit(X_train, X_train, epochs=10)  # Train using the self-supervised learning mode with X_train.
predicted_next_month_proportion = model.predict(np.array([X_train[-1]]))  # Predict the fourth month.
predicted_next_month_proportion = np.clip(predicted_next_month_proportion, 0, None)  # Prevent negative values.


# In[15]:


# Create a DataFrame to display the prediction results.
predicted_next_month_df = pd.DataFrame({
    'Category': categories,
    'Predicted_Proportion': predicted_next_month_proportion.flatten()
})


# In[16]:


print("\nPredicted Spending Proportion for Next Month:")
print(predicted_next_month_df)


# In[17]:


# Allow the user to input the total budget for the next month and allocate the budget based on the predicted proportions.
total_budget = float(input("Enter your total budget for next month: "))


# In[18]:


# Allocate the budget based on the predicted proportions.
predicted_next_month_df['Predicted_Budget'] = predicted_next_month_df['Predicted_Proportion'] * total_budget


# In[19]:


# Visualization: Predicted budget allocation for the next month.
plt.figure(figsize=(8, 6))
plt.bar(predicted_next_month_df['Category'], predicted_next_month_df['Predicted_Budget'])
plt.title(f'Predicted Budget Allocation for Next Month for User {user_id}')
plt.ylabel('Budget Allocation')
plt.xlabel('Category')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()


# In[20]:


# Output suggestions.
print("\nSuggested Budget Allocation for Next Month:")
for _, row in predicted_next_month_df.iterrows():
    print(f"{row['Category']}: {row['Predicted_Budget']:.2f}")







