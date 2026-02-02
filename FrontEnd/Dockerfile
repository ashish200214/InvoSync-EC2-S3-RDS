# -------- Build stage --------
FROM node AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# -------- Run stage --------
FROM nginx
COPY --from=build /app/dist /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
