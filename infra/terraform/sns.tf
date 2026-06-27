resource "aws_sns_topic" "track_events" {
  name = "${local.name_prefix}-track-events"
}

resource "aws_sns_topic_subscription" "track_events_to_sqs" {
  topic_arn = aws_sns_topic.track_events.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.track_events.arn
}
