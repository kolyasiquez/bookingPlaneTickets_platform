FROM nginx:alpine

# Install openssl to generate self-signed certificates
RUN apk add --no-cache openssl

# Generate self-signed SSL certificates
RUN mkdir -p /etc/nginx/ssl && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/nginx.key \
    -out /etc/nginx/ssl/nginx.crt \
    -subj "/C=PL/ST=State/L=City/O=Organization/CN=localhost"

# Copy custom configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create upload directory structure
RUN mkdir -p /app/uploads

EXPOSE 80 443
