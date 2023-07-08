import Box from "@mui/material/Box";
import CircularProgress from "@mui/material/CircularProgress";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Problem, Solution } from "./types";
import { useNavigate } from "react-router-dom";

function get(url: string) {
  return fetch(url).then((res) => res.json());
}

export const Visualization: React.FC = () => {
  const { problemId, solutionId } = useParams();
  const [problem, setProblem] = useState(null as Problem | null);
  const [solutions, setSolutions] = useState(null as Solution[] | null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      get(`http://192.168.31.106:8888/problem/${problemId}`),
      get(`http://192.168.31.106:8888/best/${problemId}/10`),
    ])
      .then(([p, s]) => {
        setProblem(p);
        setSolutions(s);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [problemId]);
  const navigate = useNavigate();

  return (
    <div className="App">
      <Box
        sx={{
          width: "100%",
          height: "100vh",
          maxWidth: "20%",
          bgcolor: "background.paper",
        }}
      >
        <Select
          value={problemId}
          label="Age"
          onChange={(e) => navigate(`/problems/${e.target.value}`)}
        >
          {Array(90)
            .fill(0)
            .map((_, i) => (
              <MenuItem value={i + 1} key={i}>
                Problem {i + 1}
              </MenuItem>
            ))}
        </Select>
        {loading ? null : (
          <Box>
            <List
              sx={{
                width: "100%",
                maxWidth: 360,
                bgcolor: "background.paper",
              }}
              component="nav"
            >
              {solutions?.map(({ id, score }) => (
                <ListItemButton
                  selected={+(solutionId || 0) === id}
                  component={Link}
                  to={`/problems/${problemId}/solutions/${id}`}
                >
                  <ListItemText primary={`${id}: score ${score}`} />
                </ListItemButton>
              ))}
            </List>
          </Box>
        )}
      </Box>
      {loading ? (
        <CircularProgress />
      ) : (
        <Box
          sx={{ width: "100%", height: "100vh", bgcolor: "background.paper" }}
        >
          <svg
            style={{ height: "100vh", width: "100%" }}
            viewBox={
              problem
                ? `0 0 ${problem?.room_width} ${problem?.room_height}`
                : undefined
            }
          >
            <rect
              stroke="black"
              strokeWidth="1"
              fill="none"
              width={problem?.room_width}
              height={problem?.room_height}
            />
            <rect
              stroke="black"
              strokeWidth="1"
              fill="rgb(200,200,200)"
              x={problem?.stage_bottom_left[0]}
              y={problem?.stage_bottom_left[1]}
              width={problem?.stage_width}
              height={problem?.stage_height}
            />
            {problem?.attendees.map((att, i) => (
              <circle
                key={i}
                cx={att.x}
                cy={att.y}
                r="5"
                fill="rgb(121,0,121)"
              />
            ))}
            {solutions
              ?.find((s) => s.id === +(solutionId || 0))
              ?.contents.placements.map((mus, i) => (
                <circle
                  key={i}
                  cx={mus.x}
                  cy={mus.y}
                  r="5"
                  fill="rgb(0,121,121)"
                />
              ))}
            {problem?.pillars.map((pillar, i) => (
              <circle
                key={i}
                cx={pillar.center[0]}
                cy={pillar.center[1]}
                r={pillar.radius}
                fill="rgb(10,10,200, 0.3)"
              />
            ))}
          </svg>
        </Box>
      )}
    </div>
  );
};
