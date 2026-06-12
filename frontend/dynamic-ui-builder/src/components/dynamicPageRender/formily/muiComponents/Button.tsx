import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { useComponentProps } from "../utils";


export const CustomButton = (props: any) => {
  const {
    text,
    style,
    variant = "contained",
    action,
    ...rest
  } = useComponentProps(props);

  return (
    <Box sx={{ display: "flex", justifyContent: "flex-start", mt: 1, mb: 2 }}>
      <Button
        {...rest}
        variant={variant}
        sx={{
          textTransform: "none",
          px: 2,
          py: 1,
          borderRadius: 2,
          ...style,
        }}
      >
        {text}
      </Button>
    </Box>
  );
};