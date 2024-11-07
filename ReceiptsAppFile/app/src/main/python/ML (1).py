#!/usr/bin/env python
# coding: utf-8

# In[4]:


import pandas as pd
import numpy as np
import tensorflow as tf
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt


# In[5]:


file_path = 'Generated_Purchases_for_Users.csv'
data = pd.read_csv(file_path)


# In[6]:


# 确保日期字段为datetime类型
data['date_purchased'] = pd.to_datetime(data['date_purchased'])


# In[7]:


# 输入用户ID并提取该用户最近三个月的数据
def get_recent_data(user_id, data, months=3):
    # 过滤指定的用户ID
    user_data = data[data['user_id'] == user_id]
    # 获取最近的日期
    latest_date = user_data['date_purchased'].max()
    # 计算最近三个月的起始日期
    start_date = latest_date - pd.DateOffset(months=months)
    # 筛选最近三个月的数据
    recent_data = user_data[(user_data['date_purchased'] >= start_date) & (user_data['date_purchased'] <= latest_date)]
    return recent_data


# In[9]:


# 输入用户ID
user_id = int(input("Enter your user ID: "))
recent_data = get_recent_data(user_id, data)


# In[10]:


# 计算每月每个类别的消费占比
recent_data['month'] = recent_data['date_purchased'].dt.to_period('M')
monthly_total = recent_data.groupby('month')['price'].sum()
monthly_spending = recent_data.groupby(['month', 'category'])['price'].sum().unstack().fillna(0)


# In[11]:


# 计算每个类别的消费占比
monthly_percentage = monthly_spending.div(monthly_total, axis=0).fillna(0)
categories = ['clothing', 'entertainment', 'electronics', 'home', 'health and personal', 'groceries']
for category in categories:
    if category not in monthly_percentage.columns:
        monthly_percentage[category] = 0


# In[12]:


# 按列的顺序确保一致
monthly_percentage = monthly_percentage[categories]


# In[13]:


# 可视化：最近三个月的消费占比趋势
monthly_percentage.plot(kind='bar', stacked=True, figsize=(10, 6))
plt.title(f"Monthly Spending Proportion per Category for User {user_id}")
plt.ylabel("Proportion of Total Spending")
plt.xlabel("Month")
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()


# In[14]:


# 准备特征用于机器学习模型
# 特征是最近三个月的消费比例
X_train = monthly_percentage.values  # 最近三个月的消费比例
if X_train.shape[0] < 3:
    X_train = np.pad(X_train, ((3 - X_train.shape[0], 0), (0, 0)), 'constant')


# In[15]:


print("X_train:")
print(X_train)


# In[16]:


# 使用TensorFlow构建模型
model = tf.keras.Sequential([
    tf.keras.layers.Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dense(len(categories), activation='softmax')  # 使用softmax输出比例
])


# In[17]:


# 编译模型
model.compile(optimizer='adam', loss='mse')


# In[18]:


# 使用模型直接预测第四个月的消费比例
model.fit(X_train, X_train, epochs=10)  # 使用X_train自监督学习模式进行训练
predicted_next_month_proportion = model.predict(np.array([X_train[-1]]))  # 预测第四个月
predicted_next_month_proportion = np.clip(predicted_next_month_proportion, 0, None)  # 防止负值


# In[19]:


# 创建 DataFrame 展示预测结果
predicted_next_month_df = pd.DataFrame({
    'Category': categories,
    'Predicted_Proportion': predicted_next_month_proportion.flatten()
})


# In[20]:


print("\nPredicted Spending Proportion for Next Month:")
display(predicted_next_month_df)


# In[21]:


# 用户输入下个月总预算，根据预测比例分配预算
total_budget = float(input("Enter your total budget for next month: "))


# In[22]:


# 根据预测的比例分配预算
predicted_next_month_df['Predicted_Budget'] = predicted_next_month_df['Predicted_Proportion'] * total_budget


# In[23]:


# 可视化：预测的下个月预算分配
plt.figure(figsize=(8, 6))
plt.bar(predicted_next_month_df['Category'], predicted_next_month_df['Predicted_Budget'])
plt.title(f'Predicted Budget Allocation for Next Month for User {user_id}')
plt.ylabel('Budget Allocation')
plt.xlabel('Category')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()


# In[24]:


# 输出建议
print("\nSuggested Budget Allocation for Next Month:")
for _, row in predicted_next_month_df.iterrows():
    print(f"{row['Category']}: {row['Predicted_Budget']:.2f}")


# In[25]:


print(X_train)


# In[ ]:




