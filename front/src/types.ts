export type Problem = {
  room_width: number;
  room_height: number;
  stage_width: number;
  stage_height: number;
  stage_bottom_left: [number, number];
  musicians: number[];
  attendees: {
    x: number;
    y: number;
    tastes: number[];
  }[];
  pillars: { center: [number, number]; radius: number }[];
};

export type Solution = {
  id: number;
  contents: {
    placements: [{ x: number; y: number }];
  };
  score: number;
  problemId: number;
};
