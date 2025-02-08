# Build stage
FROM node:20-alpine AS builder
WORKDIR /app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy source code
COPY . .

# Build application
ENV NEXT_PUBLIC_API_URL=http://localhost:8080/api
RUN npm run build

# Production stage
FROM node:20-alpine
WORKDIR /app

# Copy necessary files from builder
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

# Expose port
EXPOSE 3000

# Set environment variables
ENV NODE_ENV=production \
  PORT=3000

# Run the application
CMD ["node", "server.js"]