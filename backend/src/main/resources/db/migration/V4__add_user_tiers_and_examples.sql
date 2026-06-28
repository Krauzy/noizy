UPDATE users
SET role = 'FREE_TIER'
WHERE role = 'USER';

INSERT INTO users (id, name, email, password_hash, role)
VALUES
    ('00000000-0000-0000-0000-000000000101', 'Noizy Free One', 'free1@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'FREE_TIER'),
    ('00000000-0000-0000-0000-000000000102', 'Noizy Free Two', 'free2@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'FREE_TIER'),
    ('00000000-0000-0000-0000-000000000103', 'Noizy Free Three', 'free3@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'FREE_TIER'),
    ('00000000-0000-0000-0000-000000000201', 'Noizy Subscriber One', 'subscriber1@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'SUBSCRIBER'),
    ('00000000-0000-0000-0000-000000000202', 'Noizy Subscriber Two', 'subscriber2@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'SUBSCRIBER'),
    ('00000000-0000-0000-0000-000000000203', 'Noizy Subscriber Three', 'subscriber3@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'SUBSCRIBER'),
    ('00000000-0000-0000-0000-000000000301', 'Noizy Artist One', 'artist1@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'ARTIST'),
    ('00000000-0000-0000-0000-000000000302', 'Noizy Artist Two', 'artist2@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'ARTIST'),
    ('00000000-0000-0000-0000-000000000303', 'Noizy Artist Three', 'artist3@noizy.local', '$2a$10$Qb/SJ0zssA.YDSVpBDzoAeUh.9BHQGF381spFd0q1WAqJb23nBMIu', 'ARTIST')
ON CONFLICT (email) DO NOTHING;
