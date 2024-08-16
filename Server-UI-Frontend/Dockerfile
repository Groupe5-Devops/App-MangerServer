# Step 1: Build stage
# Use Node.js version 18 as the base image for the build stage.
FROM node:18 AS build

# Set the working directory inside the container.
WORKDIR /app

# Copy package.json and package-lock.json (if available) to the working directory.
COPY package*.json ./

# Install the npm dependencies.
RUN npm install

# Copy the rest of the source code to the working directory.
COPY . .

# Run the production build of the application.
RUN npm run build

# Step 2: Run stage
# Use the latest version of nginx as the base image for the run stage.
FROM nginx:latest

# Copy the built application from the build stage to the nginx service directory.
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the custom nginx configuration file
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Expose the port that nginx will use
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]