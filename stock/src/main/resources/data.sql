INSERT INTO player (player_id, password, name, role, player_money)
SELECT 'admin', '$2a$10$qyXUKKX9Lzu55ivTEA0ID.YkvU7q7hpJC6U.Ws1c3nsOmEHlSQt6S', 'Admin', 'ADMIN', 0
    WHERE NOT EXISTS (SELECT 1 FROM player WHERE player_id = 'admin');