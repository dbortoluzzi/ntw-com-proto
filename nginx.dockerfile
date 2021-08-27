FROM nginx
#HEALTHCHECK --interval=30s --timeout=3s \
#  CMD curl -f http://localhost/ || exit 1
COPY ./nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
