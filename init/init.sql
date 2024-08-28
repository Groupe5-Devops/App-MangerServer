-- Ensure you are using the correct database
USE `app-server`;

-- Revoke existing permissions (optional, if you want to reset all permissions)
REVOKE ALL PRIVILEGES ON `app-server`.* FROM 'app_user'@'%';

-- Grant updated permissions
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX ON `app-server`.* TO 'app_user'@'%';

-- Apply changes
FLUSH PRIVILEGES;