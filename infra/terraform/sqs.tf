resource "aws_sqs_queue" "track_events_dlq" {
  name                      = "${local.name_prefix}-track-events-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "track_events" {
  name                       = "${local.name_prefix}-track-events"
  visibility_timeout_seconds = 60
  message_retention_seconds  = 345600

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.track_events_dlq.arn
    maxReceiveCount     = 5
  })
}

data "aws_iam_policy_document" "track_events_queue_policy" {
  statement {
    actions   = ["sqs:SendMessage"]
    resources = [aws_sqs_queue.track_events.arn]

    principals {
      type        = "Service"
      identifiers = ["sns.amazonaws.com"]
    }

    condition {
      test     = "ArnEquals"
      variable = "aws:SourceArn"
      values   = [aws_sns_topic.track_events.arn]
    }
  }
}

resource "aws_sqs_queue_policy" "track_events" {
  queue_url = aws_sqs_queue.track_events.id
  policy    = data.aws_iam_policy_document.track_events_queue_policy.json
}
