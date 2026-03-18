-- if there were changes to this file, use *docker compose down -v* to stop and remove the database container, 
-- then *docker compose up* to recreate it with the new schema.
-- !IMPORTANT! This will delete all existing data in the database, so make sure to back up any important data before doing this.


-- 1. USERS TABLE
-- roles will be stored as simple string ('USER', 'ADMIN') maybe extended later if needed
-- IMPORTANT To use string not int in java code, we need to add @Enumerated(EnumType.STRING) to the role field in User entity class.

-- status will be stored as string ('ONLINE', 'OFFLINE') 

--UUID is used as primary key (randomly generated ID)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, 
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    last_seen TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. CHANNELS TABLE
-- channel_type will be stored as a string ('PUBLIC', 'PRIVATE') 
CREATE TABLE channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    channel_type VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. CHANNEL_MEMBERS TABLE (Many-to-Many relationship between users and channels)
CREATE TABLE channel_members (
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Track when a user joined a channel
    PRIMARY KEY (channel_id, user_id)
);


-- 4. MESSAGES TABLE
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
