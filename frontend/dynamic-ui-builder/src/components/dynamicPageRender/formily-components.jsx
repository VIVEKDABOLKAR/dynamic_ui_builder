import React from "react";
import { Input, Button } from "antd";

export const CustomButton = ({ text, ...props }) => {
  return <Button {...props}>{text}</Button>;
};

export const formilyComponents = {
  Input,
  CustomButton,
};