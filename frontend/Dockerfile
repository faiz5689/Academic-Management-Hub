# Use Node.js as the base image
FROM node:20-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json first
# This is a best practice as it allows Docker to cache dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all other source code files
COPY . .

# Build the Next.js application
RUN npm run build

# Expose port 3000 to tell Docker this container will listen on this port
EXPOSE 3000

# Command to run the application
CMD ["npm", "start"]