

# 📘 **InvoSync – Quotation & Architecture Workflow Management System**

InvoSync is a full-stack application designed for organizations that generate **quotations**, prepare **site architecture drawings**, collect **project images/videos**, and send **final bills** to clients.  
The system supports:

✅ Quotation creation (frontend form)  
✅ Auto generation of architecture plan + bill by organization  
✅ Uploading drawings, PDFs, images, and videos  
✅ Storing all project files in **Amazon S3** with an organized folder structure  
✅ Managing all quotation details in **Amazon RDS (MySQL)**  
✅ Backend REST APIs using **Spring Boot**  
✅ Frontend UI using **React (Vite)**  
✅ Deployment on **AWS EC2 with PM2**  

---
<img width="1920" height="1008" alt="Screenshot 2025-11-14 223439" src="https://github.com/user-attachments/assets/30dad91c-c080-40e1-838d-3c295fbd569d" />
<img width="1920" height="1008" alt="Screenshot 2025-11-14 223448" src="https://github.com/user-attachments/assets/a3f33636-b93c-4771-840d-7f5855e050b6" />
<img width="1920" height="1008" alt="Screenshot 2025-11-14 223540" src="https://github.com/user-attachments/assets/b6a476bc-76df-4d39-9234-0a23f143e87b" />
<img width="1920" height="1008" alt="Screenshot 2025-11-14 225029" src="https://github.com/user-attachments/assets/e1b3a7c3-2828-44a8-8f9a-250eeaa0b287" />

## 🏗️ **System Architecture**

```
Frontend (React:5173) → Backend API (Spring Boot:9090) → RDS (MySQL)
                                               ↳ S3 (all drawings/files)
```

---

## 📁 S3 Storage Structure

Each quotation is stored under the client’s **mobile number** as the parent folder:

```
s3://your-bucket/
│
└── 9876543210/    
     ├── drawings/
     │     └── architecture-plan.pdf
     │     └── blueprint.png
     │
     ├── images/
     │     └── site1.jpg
     │     └── site2.png
     │
     └── videos/
           └── walkaround.mp4
```

This ensures clean grouping of all files related to each customer.

---

## 🛢️ **Database (Amazon RDS – MySQL)**

Stores:

- Customer details
- Quotation fields
- File URLs from S3
- Architecture/billing data

---

## 🚀 **Features**

✔ React form to create quotations  
✔ Store quotations in RDS  
✔ Upload multiple image/video/pdf files  
✔ All files stored in S3 with mobile-based folder structure  
✔ Organization uploads architecture plan + bill  
✔ Backend generates accessible URLs  
✔ List all uploaded files  
✔ View or download them through UI  
✔ Fully deployed on AWS EC2 using PM2  

---

---

# ⚙️ **Technologies Used**

| Component        | Technology |
|------------------|------------|
| Frontend         | React + Vite + Axios |
| Backend          | Spring Boot 3.5+ |
| Database         | Amazon RDS (MySQL) |
| File Storage     | Amazon S3 |
| Deployment       | AWS EC2 (Amazon Linux 2023) |
| Process Manager  | PM2 |
| Build Tools      | Maven |
| Language         | Java 21 |

---

---

# 🖥️ **Frontend Setup (React + Vite)**

### **1. Install Node.js**
```bash
sudo dnf install nodejs -y
```

### **2. Install npm**
```bash
sudo dnf install npm -y
```

### **3. Install dependencies**
```bash
cd FrontEnd
npm install
```

### **4. Install PM2 globally**
```bash
npm install -g pm2
```

### **5. Run Frontend in background**
```bash
pm2 start npm --name "invosync-frontend" -- run dev -- --host 0.0.0.0
```

Now the frontend runs on:

```
http://EC2_PUBLIC_IP:5173
```

---

---

# 🛠️ **Backend Setup (Spring Boot + Maven)**

### **1. Install Maven**
```bash
sudo dnf install maven -y
```

### **2. Build the JAR file**
```bash
mvn clean package -DskipTests
```

### **3. Run Spring Boot Application in background using PM2**

```bash
pm2 start "java -jar target/invoSync-0.0.1-SNAPSHOT.jar --server.port=9090 --server.address=0.0.0.0" --name springapp --update-env
```

OR (full absolute path):

```bash
pm2 start "java -jar /home/ec2-user/Invo_Sync_Project/BackEnd/target/invoSync-0.0.1-SNAPSHOT.jar --server.port=9090 --server.address=0.0.0.0" --name springapp --update-env
```

Your APIs now work at:

```
http://EC2_PUBLIC_IP:9090/api/quotations/
```

---

---

# 📡 **Networking Requirements**

Make sure the EC2 security group allows:

| Port | Service | Purpose |
|------|---------|---------|
| 5173 | React (Vite) | Frontend |
| 9090 | Spring Boot | Backend REST API |
| 3306 | MySQL | RDS connection (in private VPC) |

Open ports:

```bash
sudo firewall-cmd --add-port=9090/tcp --permanent
sudo firewall-cmd --add-port=5173/tcp --permanent
sudo firewall-cmd --reload
```

---

---

# 🔐 **Environment Variables**

Your Spring Boot app uses:

```
spring.datasource.url=jdbc:mysql://<rds-endpoint>:3306/mydb
spring.datasource.username=<your-user>
spring.datasource.password=<your-password>

aws.s3.bucket=s3lempworkflow
aws.region=us-east-1
```

---

---

# 📸 **File Upload Workflow**

1. User fills a quotation form in React  
2. React sends data to Spring Boot  
3. Spring Boot saves quotation into RDS  
4. User uploads drawings/photos/videos  
5. Backend stores them in S3 as:

```
mobileno/drawings/
mobileno/images/
mobileno/videos/
mobileno/pdf/
```

6. Final quotation PDF is generated and uploaded into:

```
mobileno/pdf/
```

---

---

# 📝 **Project Folder Structure**

```
InvoSync_Project/
├── FrontEnd/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
└── BackEnd/
    ├── src/main/java/com/asent/invoSync/
    ├── src/main/resources/application.properties
    ├── pom.xml
    └── target/invoSync-0.0.1-SNAPSHOT.jar
```

---

---

# 🧪 Testing API

```bash
curl -v http://EC2_PUBLIC_IP:9090/api/quotations/
```

---

---

# 🚀 Deployment Summary

| Component | Running on | Command |
|----------|------------|---------|
| Frontend | EC2 (port 5173) | `pm2 start npm -- run dev` |
| Backend | EC2 (port 9090) | `pm2 start "java -jar ..."` |
| DB | Amazon RDS (MySQL) | internal connection |
| File Storage | Amazon S3 | via AWS SDK |

---

---

# 🎯 Conclusion

InvoSync provides a complete automated workflow for:

✔ Quotation creation  
✔ Architectural plan + drawing uploads  
✔ Photo/video storage  
✔ PDF generation  
✔ Secure S3 & RDS integration  
✔ Full-stack AWS deployment  

