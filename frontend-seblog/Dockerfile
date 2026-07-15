# ==========================================
# ЭТАП 1: Сборка Angular приложения
# ==========================================
FROM node:20-alpine AS build
WORKDIR /app

# Копируем package.json и устанавливаем зависимости
COPY package*.json ./
RUN npm ci

# Копируем исходный код и собираем проект
COPY . .
RUN npm run build

# ==========================================
# ЭТАП 2: Развертывание на Nginx
# ==========================================
FROM nginx:alpine

COPY --from=build /app/dist/frontend-seblog/browser /usr/share/nginx/html

# Открываем порт
EXPOSE 80

# Запускаем Nginx
CMD ["nginx", "-g", "daemon off;"]