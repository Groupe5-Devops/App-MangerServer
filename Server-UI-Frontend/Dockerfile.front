#Build stage
FROM node:18-buster AS build

WORKDIR /app

COPY package*.json ./
# Install the npm dependencies.

RUN npm install 

# Copy the rest of the source code to the working directory.
COPY . .
# Run the production build of the application.
ARG configuration=production
 
RUN npm run build -outputPath=/app/dist/ --configuration=$configuration

# Run stage
FROM nginx:stable-alpine

COPY --from=build /app/dist /usr/share/nginx/html

COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]