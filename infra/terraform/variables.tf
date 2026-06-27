variable "project_name" {
  description = "Project name used for AWS resource names."
  type        = string
  default     = "noizy"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "AWS region."
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR range for the VPC."
  type        = string
  default     = "10.40.0.0/16"
}

variable "az_count" {
  description = "Number of availability zones to use."
  type        = number
  default     = 2
}

variable "database_name" {
  description = "PostgreSQL database name."
  type        = string
  default     = "noizy"
}

variable "database_username" {
  description = "PostgreSQL master username."
  type        = string
  default     = "noizy"
}

variable "database_password" {
  description = "PostgreSQL master password. Pass through TF_VAR_database_password or a secrets workflow."
  type        = string
  sensitive   = true
}

variable "node_desired_size" {
  description = "Desired EKS managed node count."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum EKS managed node count."
  type        = number
  default     = 2
}

variable "node_max_size" {
  description = "Maximum EKS managed node count."
  type        = number
  default     = 6
}

variable "tags" {
  description = "Additional tags applied to AWS resources."
  type        = map(string)
  default     = {}
}

locals {
  name_prefix = "${var.project_name}-${var.environment}"
  tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    },
    var.tags
  )
}
