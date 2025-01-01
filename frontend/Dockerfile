# Stage 1: Compile and Build angular codebase

# Use a stable version of Node.js LTS image as the base image
FROM node:18 as build

# Set the working directory for the build stage
WORKDIR /usr/local/app

# Copy package.json and package-lock.json first to leverage Docker's cache
COPY package.json package-lock.json ./

# Install the dependencies (npm install will be faster due to Docker cache)
RUN npm install

# Copy the rest of the application code into the container
COPY ./ /usr/local/app/

# Generate the build of the Angular application
RUN npm run build --prod

# Stage 2: Serve app with nginx server

# Use the official nginx image as the base image for serving the app
FROM nginx:latest

# Copy the build output from the build stage to the nginx default directory
COPY --from=build /usr/local/app/dist/frontend/browser /usr/share/nginx/html

# Expose port 80 (the default port for nginx)
EXPOSE 80

# Ensure nginx runs in the foreground
CMD ["nginx", "-g", "daemon off;"]
