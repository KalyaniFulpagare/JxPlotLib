CREATE TABLE chart_data (
    label VARCHAR(50),
    x_value DOUBLE,
    y_value DOUBLE
);

INSERT INTO chart_data (label, x_value, y_value) VALUES
('Q1', 1, 18),
('Q2', 2, 26),
('Q3', 3, 23),
('Q4', 4, 34);

CREATE TABLE heatmap_data (
    row_key VARCHAR(50),
    column_key VARCHAR(50),
    value DOUBLE
);

INSERT INTO heatmap_data (row_key, column_key, value) VALUES
('Jan', 'North', 22),
('Jan', 'South', 28),
('Jan', 'East', 31),
('Jan', 'West', 17),
('Feb', 'North', 26),
('Feb', 'South', 30),
('Feb', 'East', 34),
('Feb', 'West', 21),
('Mar', 'North', 29),
('Mar', 'South', 35),
('Mar', 'East', 40),
('Mar', 'West', 24);
