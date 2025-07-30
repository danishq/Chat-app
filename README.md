# 💬 Hey There! Check Out This Chat App!

This is a cool, real-time **private chat app** built with **Spring Boot**. It features user logins, persistent messages, and smooth real-time messaging on the web. A neat peek into **full-stack Spring development**!

---

## 🚀 What Can It Do?

- **🔐 User Logins**  
  Sign up and log in easily. Your session stays active until you log out, keeping chats private.

- **📬 Private Chats**  
  Send messages directly to another user. They go straight to the recipient (and you!), keeping conversations secure.

- **⚡ Real-time Fun!**  
  Messages pop up instantly using WebSockets. No refreshing needed – just like your favorite messaging apps!

- **💾 Your Messages Stick Around**  
  All messages and accounts are saved in a PostgreSQL database. Your chat history is always there, even after logging out.

- **📱 Looks Good Anywhere**  
  Basic responsive web design that works well on phones, tablets, or computers.

- **🆕 Newest Messages on Top**  
  Latest messages appear first. Scroll down to see older ones, making it easy to catch up!

---

## 🔧 What's Under the Hood?

### 🖥️ Tech Stack

- **Backend**  
  `Spring Boot`, `Spring Web`, `Spring Data JPA`, `Spring WebSockets`, `Thymeleaf`, `Hibernate`, `PostgreSQL`, `Maven`

- **Frontend**  
  `HTML5`, `CSS3`, `JavaScript`, `SockJS`, `STOMP.js`

---

## 🛠 Getting Started (Easy Peasy!)

### ✅ Requirements

- Java Development Kit (JDK) **17+**
- Apache Maven **3.6+**
- PostgreSQL Database
- Git

---

## ⚙️ Let's Get This Thing Running!

### 1. 🔽 Grab the Code!

```bash
git clone github.com/danishq/Chat-app.git
cd chat-app
```

---

### 2. 🛢️ Set Up Your PostgreSQL Database

Log into PostgreSQL:

```bash
psql -U postgres
```

Run these SQL commands:

```sql
CREATE USER user WITH PASSWORD '12345';
CREATE DATABASE chatdb OWNER user;
GRANT ALL PRIVILEGES ON DATABASE chatdb TO user;
\q
```

---

### 3. 📝 Check `application.properties`

Make sure `src/main/resources/application.properties` has these values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatdb
spring.datasource.username=user
spring.datasource.password=12345
spring.jpa.hibernate.ddl-auto=update
```

---

### 4. ▶️ Fire Up the App

In the `chat-app` folder, run:

```bash
./mvnw spring-boot:run
```

You should see:

```
Tomcat started on port 8080
```

---

## 💻 How to Use It!

1. Open [http://localhost:8080/](http://localhost:8080/) in your browser.
2. Click **"Register here"** and create at least two accounts (e.g., `alice/1234`, `bob/1234`).
3. Log in as **Alice** in one tab.
4. Log in as **Bob** in another tab (Incognito mode recommended).

### ✉️ Sending Messages

- In Alice's chat, type `bob` in the "To Username" field, type your message, and click **Send**.
- The message appears at the **top** of both Alice’s and Bob’s chats — **instantly**!

### 🔄 Receiving Messages

- Bob can reply to Alice.
- Alice sees Bob’s message instantly.

> 🕵️ Private Note: Chats are **private**! Alice won't see messages between Charlie and David.

### 🚪 Time to Log Out

Click **"Logout"** on the chat page.

---

## ⚠️ Just a Heads Up!

- **🔐 Security Warning**:  
  Passwords are stored in **plain text**. For production, use **BCrypt** and **Spring Security**.

- **⚠️ Basic Error Handling**:  
  The app has minimal error messages. Real apps need more user-friendly feedback.

- **⚙️ Scaling Concerns**:  
  For many users, you'll need messaging systems like **RabbitMQ** or **Kafka**, load balancing, and database scaling.

---

🧪 Just for **learning purposes** – explore, break things, and build something amazing!
